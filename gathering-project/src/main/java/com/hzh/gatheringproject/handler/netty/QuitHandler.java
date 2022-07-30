package com.hzh.gatheringproject.handler.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import com.hzh.gatheringproject.session.factory.*;
import org.springframework.stereotype.Component;

/**
 * @author DAHUANG
 * @date 2022/7/25
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class QuitHandler extends ChannelInboundHandlerAdapter {

    //当连接断开时触发 inactive 事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开",ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经异常断开 异常是{}",ctx.channel(),cause.getMessage());
    }
}
