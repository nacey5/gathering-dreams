package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.message.GroupMembersRequestMessage;
import com.hzh.gatheringproject.message.GroupMembersResponseMessage;
import com.hzh.gatheringproject.session.service.GroupSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import com.hzh.gatheringproject.session.factory.*;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author DAHUANG
 * @date 2022/7/24
 */
@ChannelHandler.Sharable
@Component
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    //进行成员查找
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(msg.getGroupName());
        if (members.isEmpty()) {
            ctx.writeAndFlush("没有此群没有任何成员");
        }else {
            ctx.writeAndFlush(new GroupMembersResponseMessage(members));
        }
    }
}
