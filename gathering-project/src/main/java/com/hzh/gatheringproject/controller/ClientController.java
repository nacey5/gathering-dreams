package com.hzh.gatheringproject.controller;

import com.hzh.gatheringproject.config.NettyRegConfig;
import com.hzh.gatheringproject.dto.GroupDto;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.Group;
import com.hzh.gatheringproject.message.*;
import com.hzh.gatheringproject.protocol.MessageCodecSharable;
import com.hzh.gatheringproject.protocol.ProcotolFrameDecoder;
import com.hzh.gatheringproject.session.factory.GroupSessionFactory;
import com.hzh.gatheringproject.util.UserHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DAHUANG
 * @date 2022/7/28
 */
@RestController
@Slf4j
@RequestMapping("/chatSocket")
public class ClientController {

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    ChannelHandlerContext chx=null;

    public static final ConcurrentHashMap<String, Queue> messageConcurrentHashMap=new ConcurrentHashMap<>(16);

    @Async("asyncTaskExecutor")
    public void connectToClient(String userId){
        log.warn("====客户端给正在尝试开启.....===============");

        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN=new CountDownLatch(1);
        AtomicBoolean LOGIN=new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,1000);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    log.warn("====客户端尝试初始化====================");
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //3s内如果向服务器写数据，会触发一个IdleState事件#WRITE_IDLE事件
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    //ChannelDuplexHandler 可以同时作为入站处理器和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        //用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event= (IdleStateEvent) evt;
                            //触发了读空闲事件
                            if (event.state()== IdleState.WRITER_IDLE) {
//                                log.debug("3s 没有写数据，发送心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });

                    //为客户端添加激活处理器
                    ch.pipeline().addLast("client-handler",new ChannelInboundHandlerAdapter(){
                        //接收相应信息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.warn("msg:{}",msg);
                            if ((msg instanceof LoginResponseMessage)) {
                                LoginResponseMessage responseMessage= (LoginResponseMessage) msg;
                                if (responseMessage.isSuccess()) {
                                    chx=ctx;
                                    BlockingQueue<Message> queue=new LinkedBlockingDeque<>(1024);
                                    if (!messageConcurrentHashMap.containsKey(((LoginResponseMessage) msg).getReason())){
                                        //当登陆成功的时候，reason中保存的是登陆用户的用户id
                                        messageConcurrentHashMap.put(((LoginResponseMessage) msg).getReason()+"",queue);
                                        //如果登陆成功
                                        LOGIN.set(true);
                                    }
                                }
                                //唤醒system-in线程
                                WAIT_FOR_LOGIN.countDown();
                            }
                            if (msg instanceof ChatResponseMessage){
                                //往前台发送接收到的信息并显示-->根据此方式就ok
                                ChatResponseMessage responseMessage= (ChatResponseMessage) msg;
                                //Queue queue = messageConcurrentHashMap.get(userId);建议这种方式，到时候两个一起测试
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                                messageConcurrentHashMap.put(userId,queue);
                            }
                            if (msg instanceof GroupCreateResponseMessage){
                                //往前台发送接收到的信息并显示
                                GroupCreateResponseMessage responseMessage= (GroupCreateResponseMessage) msg;
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                                //创建群聊信息，所有消息放回前台的时候根据类型进行处理
                            }
                            if (msg instanceof GroupMembersResponseMessage){
                                //往前台发送接收到的信息并显示
                                GroupMembersResponseMessage responseMessage= (GroupMembersResponseMessage) msg;
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                                //查看成员的消息
                            }
                            if (msg instanceof GroupQuitResponseMessage){
                                GroupQuitResponseMessage responseMessage= (GroupQuitResponseMessage) msg;
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                                //关闭群聊
                            }
                            if (msg instanceof GroupChatResponseMessage){
                                //群聊信息
                                GroupChatResponseMessage responseMessage= (GroupChatResponseMessage) msg;
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                            }
                            if (msg instanceof GroupJoinResponseMessage){
                                //加入群聊
                                GroupJoinResponseMessage responseMessage= (GroupJoinResponseMessage) msg;
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                            }
                            if (msg instanceof GroupMembersResponseMessage){
                                //加入群聊
                                GroupMembersResponseMessage responseMessage= (GroupMembersResponseMessage) msg;
                                Queue queue = messageConcurrentHashMap.get(userId);
                                queue.add(responseMessage);
                            }

                        }
                        //在连接建立后触发active事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //创立一个新的线程接收用户输入,负责向服务器发送各种消息

                            Thread thread = threadPoolTaskExecutor.newThread(new Runnable() {
                                @Override
                                public void run() {
                                    String userID = userId;
                                    //构造消息对象
                                    LoginRequestMessage message = new LoginRequestMessage(userID+"");
                                    //发送消息
                                    ctx.writeAndFlush(message);
                                    try {
                                        WAIT_FOR_LOGIN.await();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!LOGIN.get()) {
                                        ctx.channel().close();
                                        return;
                                    }
                                }
                            });
                            thread.setName("chat-client"+UUID.randomUUID());
                            thread.start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("127.0.0.1", NettyRegConfig.NETTY_SERVER).sync().channel();
            channel.closeFuture().sync();
        }
        catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }


    @PostMapping("/chatSingle")
    public Result chatOneUser(@RequestParam("content") String content,@RequestParam("id") String toUserId,@RequestParam("userId")String userId){
        try {
            String from=UserHolder.getUser().getName();
            String to=toUserId;
            ChatRequestMessage msg = new ChatRequestMessage(from, to, content);
            /*Channel channel = SessionFactory.getSession().getChannel(userId);*/
            chx.writeAndFlush(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("未知错误，请重试");
        }
        return Result.ok("发送成功");
    }

    @PostMapping("/getMyChat")
    public Result getMyChat(@RequestParam("userId") String userId){
        Queue queue = messageConcurrentHashMap.get(userId);
        LinkedBlockingQueue ret = new LinkedBlockingQueue<>(queue);
        queue.clear();
        return Result.ok(ret) ;
    }

    @PostMapping("/createGroup")
    public Result createGroup(@RequestBody GroupDto groupInfo){
        try {
            Set<String> set=new HashSet<>(Arrays.asList(groupInfo.getIds()));
            set.add(groupInfo.getId()+"");
            GroupCreateRequestMessage msg = new GroupCreateRequestMessage(groupInfo.getGroupName(),set);
            /*Channel channel = SessionFactory.getSession().getChannel(userId);*/
            chx.writeAndFlush(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("未知错误，请重试");
        }
        return Result.ok("群聊创建成功，请刷新");
    }

    @PostMapping("/quitGroup")
    public Result quitGroup(@RequestParam("userId") String userId,@RequestParam("groupName")String groupName){
        try {
            chx.writeAndFlush(new GroupQuitRequestMessage(userId,groupName));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("未知错误,已经关闭");
        }
        return Result.ok("群聊关闭成功,请刷新");
    }

    @PostMapping("/sendGroupMessage")
    public Result sendGroupMessage(@RequestParam("userId")String userId,@RequestParam("groupName")String groupName,@RequestParam("content")String content){
        try {
            chx.writeAndFlush(new GroupChatRequestMessage(userId,groupName,content));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("未知错误,请重试");
        }
        return Result.ok("群发完成");
    }

    @PostMapping("/joinGroup")
    public Result joinGroup(@RequestParam("userId")String userId,@RequestParam("groupName") String groupName){
        try {
            chx.writeAndFlush(new GroupJoinRequestMessage(userId,groupName));

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("未知错误，请重试");
        }
        return Result.ok("操作成功");
    }

    @PostMapping("/getMembersForGroup")
    public Result getMembersForGroup(@RequestParam("groupName") String groupName){
        Set<String> members=null;
        try {
            chx.writeAndFlush(new GroupMembersRequestMessage(groupName));
            members= GroupSessionFactory.getGroupSession().getMembers(groupName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("未知错误，请重试");
        }
        return Result.ok(members);
    }

}