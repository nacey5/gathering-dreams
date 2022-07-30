package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.entity.Group;
import com.hzh.gatheringproject.message.GroupCreateRequestMessage;
import com.hzh.gatheringproject.message.GroupCreateResponseMessage;
import com.hzh.gatheringproject.session.service.GroupSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import com.hzh.gatheringproject.session.factory.*;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author DAHUANG
 * @date 2022/7/24
 */
@ChannelHandler.Sharable
@Component
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group==null) {
            //发送拉群消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入"+groupName));
            }
            //发送成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建成功"));
        }else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName+"已经存在"));
        }

    }
}
