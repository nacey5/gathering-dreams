package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.entity.Group;
import com.hzh.gatheringproject.message.GroupQuitRequestMessage;
import com.hzh.gatheringproject.message.GroupQuitResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import com.hzh.gatheringproject.session.factory.*;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

/**
 * @author DAHUANG
 * @date 2022/7/24
 */
@ChannelHandler.Sharable
@Component
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String username = msg.getUsername();
        Group group = GroupSessionFactory.getGroupSession().removeMember(groupName, username);
        if (group==null) {
            ctx.writeAndFlush(new GroupQuitResponseMessage(false,"群已不存在"));
        }else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true,"退出群成功"));
        }
    }
}
