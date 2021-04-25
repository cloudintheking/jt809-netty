package io.github.cloudintheking.jt809.server.handler;

import io.github.cloudintheking.jt809.utils.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 认证处理器
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {
    public static final AuthHandler INSTANCE = new AuthHandler();

    private AuthHandler() {

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!SessionUtil.hasLogin(ctx.channel())) {
            ctx.channel().close(); //未登录关闭连接
        } else {
            ctx.pipeline().remove(this);//已登录移除该处理器
            super.channelRead(ctx, msg);
        }
    }
}
