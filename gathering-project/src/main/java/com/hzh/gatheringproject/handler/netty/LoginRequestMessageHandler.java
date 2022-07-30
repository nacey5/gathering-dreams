package com.hzh.gatheringproject.handler.netty;

import com.hzh.gatheringproject.message.LoginRequestMessage;
import com.hzh.gatheringproject.message.LoginResponseMessage;
import com.hzh.gatheringproject.session.service.Session;
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
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String userID = msg.getUserID();
        //这个是伴随登陆业务的绑定业务
        /*boolean login = UserServiceFactory.getUserService().login(userName, password);*/
        LoginResponseMessage message = null;
        if (true) {//登陆成功
            Session session = SessionFactory.getSession();
            session.bind(ctx.channel(), userID);
            message = new LoginResponseMessage(true, userID+"");
        }
        ctx.writeAndFlush(message);
    }
}
