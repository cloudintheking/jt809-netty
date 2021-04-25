package io.github.cloudintheking.jt809.client.handler;


import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 心跳处理
 */
public class HeartBeatHandler extends IdleStateHandler {

    private static Logger LOG = LoggerFactory.getLogger(HeartBeatHandler.class);

    private static final int WRITE_IDLE_TIME = 60;

    public HeartBeatHandler() {
        super(0, WRITE_IDLE_TIME, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        if (e.state() == IdleState.WRITER_IDLE) {
            LOG.info("链路空闲,发送心跳!");
            Message msg = new Message(MsgIdConstant.UP_LINKETEST_REQ);
            ByteBuf channelBuffer = Unpooled.buffer(0);
            msg.setMsgBody(channelBuffer);
            ctx.channel().writeAndFlush(msg);
            super.channelIdle(ctx, e);
        }
    }

}
