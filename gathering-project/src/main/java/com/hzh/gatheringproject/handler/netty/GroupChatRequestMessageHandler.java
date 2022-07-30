package com.hzh.gatheringproject.handler.netty;


import com.hzh.gatheringproject.message.GroupChatRequestMessage;
import com.hzh.gatheringproject.message.GroupChatResponseMessage;
import com.hzh.gatheringproject.session.service.GroupSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.hzh.gatheringproject.session.factory.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author DAHUANG
 * @date 2022/7/24
 */
@ChannelHandler.Sharable
@Component
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String content = msg.getContent();
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
        for (Channel channel : membersChannel) {
            channel.writeAndFlush(new GroupChatResponseMessage(from,content));
        }
    }
}
