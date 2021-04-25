package io.github.cloudintheking.jt809.server.handler;

import io.github.cloudintheking.jt809.protocol.Message;
import io.github.cloudintheking.jt809.service.BusinessService;
import io.github.cloudintheking.jt809.utils.ApplicationContextProvider;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

/**
 * 业务处理器
 */
@ChannelHandler.Sharable
public class BusinessHandler extends SimpleChannelInboundHandler<Message> {

    public static BusinessHandler INSTANCE = new BusinessHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        Map<String, BusinessService> businessServiceMap = ApplicationContextProvider.getBeansByClass(BusinessService.class);
        for (BusinessService businessService : businessServiceMap.values()) {
            if (businessService.support(message.getMsgId())) {
                businessService.handle(channelHandlerContext, message);
            }
        }

    }
}
