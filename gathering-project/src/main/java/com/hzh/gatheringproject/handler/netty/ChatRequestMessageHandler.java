package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.message.ChatRequestMessage;
import com.hzh.gatheringproject.message.ChatResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.hzh.gatheringproject.session.factory.*;
import org.springframework.stereotype.Component;


/**
 * @author DAHUANG
 * @date 2022/7/24
 */
@ChannelHandler.Sharable
@Component
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        //在线
        if (channel!=null){
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }
        //不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方用户不存在或者不在线"));
        }
    }
}
