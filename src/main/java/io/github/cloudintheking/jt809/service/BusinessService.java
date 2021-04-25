package io.github.cloudintheking.jt809.service;

import io.github.cloudintheking.jt809.protocol.Message;
import io.netty.channel.ChannelHandlerContext;

public interface BusinessService {


    void handle(ChannelHandlerContext channelHandlerContext, Message message);

    boolean support(int msgId);


}
