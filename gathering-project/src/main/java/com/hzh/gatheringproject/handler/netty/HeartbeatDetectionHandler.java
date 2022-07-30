package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.message.PingMessage;
import com.hzh.gatheringproject.message.PongMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;


/**
 * @author DAHUANG
 * @date 2022/7/25
 */
@ChannelHandler.Sharable
@Component
public class HeartbeatDetectionHandler extends SimpleChannelInboundHandler<PingMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        ctx.writeAndFlush(new PongMessage());
    }
}
