package io.github.cloudintheking.jt809.client.handler;


import io.github.cloudintheking.jt809.attribute.Attributes;
import io.github.cloudintheking.jt809.attribute.Session;
import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 登陆响应处理
 */
@Slf4j
public class LoginResponseHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message msg) throws Exception {
        if (msg.getMsgId() == MsgIdConstant.UP_CONNECT_RSP) {
            log.info("接收来自server登陆响应");
            ByteBuf msgBody = msg.getMsgBody();
            int result = msgBody.readByte();
            if (result == MsgIdConstant.UP_CONNECT_RSP_SUCCESS) {
                channelHandlerContext.channel().attr(Attributes.SESSION).set(Session.builder().build());
                log.info("登录成功");
            } else {
                log.error("登录异常，请检查0x0:{}", Integer.toHexString(result));
            }
        }
    }
}
