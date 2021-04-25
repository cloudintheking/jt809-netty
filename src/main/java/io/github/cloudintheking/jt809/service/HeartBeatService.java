package io.github.cloudintheking.jt809.service;

import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.protocol.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

@Service
public class HeartBeatService implements BusinessService {
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, Message message) {
        Message msgRep = new Message(MsgIdConstant.UP_LINK_TEST_RSP);
        msgRep.setMsgBody(Unpooled.buffer(0));
        channelHandlerContext.channel().writeAndFlush(msgRep);
    }

    @Override
    public boolean support(int msgId) {
        return msgId == MsgIdConstant.UP_LINKETEST_REQ;
    }
}
