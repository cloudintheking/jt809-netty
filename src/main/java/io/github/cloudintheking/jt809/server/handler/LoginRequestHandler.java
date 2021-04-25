package io.github.cloudintheking.jt809.server.handler;

import io.github.cloudintheking.jt809.attribute.Attributes;
import io.github.cloudintheking.jt809.attribute.Session;
import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.protocol.Message;
import io.github.cloudintheking.jt809.protocol.response.LoginResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 登陆处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<Message> {

    public static LoginRequestHandler INSTANCE = new LoginRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message msg) throws Exception {

        if (msg.getMsgId() == MsgIdConstant.UP_CONNECT_REQ) {
            //检查msgGnsscenterId是否正确
            ByteBuf body = Unpooled.copiedBuffer(msg.getMsgBody());
            int userId = body.readInt();
            String passWord = body.readBytes(8).toString(Charset.forName("GBK"));
            String ip = body.readBytes(32).toString(Charset.forName("GBK"));
            int port = body.readUnsignedShort();
            //检测用户名密码是否正确
            channelHandlerContext.channel().attr(Attributes.SESSION).set(Session.builder()
                    .userId(userId)
                    .password(passWord)
                    .build());
            log.info("用户:{}登陆成功", userId);
            Message msgRep = new Message(MsgIdConstant.UP_CONNECT_RSP);
            LoginResponse response = new LoginResponse();
            response.setResult(0);
            response.setVerifyCode(1);
            msgRep.setMsgBody(response.toByteBuf());
            channelHandlerContext.channel().writeAndFlush(msgRep);
        }
        channelHandlerContext.fireChannelRead(msg);
    }
}
