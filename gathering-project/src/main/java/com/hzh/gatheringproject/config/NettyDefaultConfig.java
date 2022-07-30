package com.hzh.gatheringproject.config;

import com.hzh.gatheringproject.handler.netty.*;
import com.hzh.gatheringproject.protocol.MessageCodecSharable;
import com.hzh.gatheringproject.protocol.ProcotolFrameDecoder;
import com.hzh.gatheringproject.protocol.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

/**
 * @author DAHUANG
 * @date 2022/7/27
 */
@Configuration
@Slf4j
public class NettyDefaultConfig{

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Value("${serializer.algorithm}")
    public static String serializerValue;

    NioEventLoopGroup boss = new NioEventLoopGroup();
    NioEventLoopGroup worker = new NioEventLoopGroup();
    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

    @Autowired
    MessageCodecSharable MESSAGE_CODEC ;
    @Autowired
    LoginRequestMessageHandler LOGIN_HANDLER ;
    @Autowired
    ChatRequestMessageHandler CHAT_HANDLER;
    @Autowired
    GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER;
    @Autowired
    GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER ;
    @Autowired
    GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER ;
    @Autowired
    GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER;
    @Autowired
    GroupChatRequestMessageHandler GROUP_CHAT_HANDLER ;
    @Autowired
    QuitHandler QUIT_HANDLER ;
    @Autowired
    HeartbeatDetectionHandler HEART_HANDLER;


    public static Serializer.Algorithm getSerializerAlgorithm() {
        if(serializerValue == null|| "Java".equals(serializerValue)) {
            return Serializer.Algorithm.Java;
        } else {
            return Serializer.Algorithm.valueOf(serializerValue);
        }
    }


    @PostConstruct
    public void startNettyServer(){
        Thread thread = threadPoolTaskExecutor.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.warn("正在加载netty启动器");
                    ServerBootstrap serverBootstrap = new ServerBootstrap();
                    serverBootstrap.channel(NioServerSocketChannel.class);
                    serverBootstrap.group(boss, worker);
                    serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            /*ch.pipeline().addLast(LOGGING_HANDLER);*/
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(HEART_HANDLER);
                            //5s内如果没有收到channel的数据，会触发一个IdleState事件#READER_IDLE事件
                            ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                            //ChannelDuplexHandler 可以同时作为入站处理器和出站处理器
                            ch.pipeline().addLast(new ChannelDuplexHandler(){
                                //用来触发特殊事件
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event= (IdleStateEvent) evt;
                                    //触发了读空闲事件
                                    if (event.state()== IdleState.READER_IDLE) {
                                        log.debug("已经5s没有收到数据了");
                                    }
                                }
                            });
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            ch.pipeline().addLast(CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                            ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                            ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                            ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                            ch.pipeline().addLast(QUIT_HANDLER);
                        }
                    });
                    Channel channel = serverBootstrap.bind(NettyRegConfig.NETTY_SERVER).sync().channel();
                    channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("server error", e);
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        });
        thread.setName("chat-server");
        thread.start();
    }

}
