package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.entity.Group;
import com.hzh.gatheringproject.message.GroupJoinRequestMessage;
import com.hzh.gatheringproject.message.GroupJoinResponseMessage;
import com.hzh.gatheringproject.session.service.GroupSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import com.hzh.gatheringproject.session.factory.*;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author DAHUANG
 * @date 2022/7/24
 */
@ChannelHandler.Sharable
@Component
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String username = msg.getUsername();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.joinMember(groupName, username);
        if (group!=null){//加群成功
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupJoinResponseMessage(true,username+"已加入群聊"+groupName));
            }
            ctx.writeAndFlush(new GroupJoinResponseMessage(true,"加入群聊"+groupName+"成功"));
        }else{//加群失败，群不存在
            ctx.writeAndFlush(new GroupJoinResponseMessage(false,groupName+"不存在"));
        }
    }
}
