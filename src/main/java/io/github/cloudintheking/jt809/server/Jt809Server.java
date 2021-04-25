package io.github.cloudintheking.jt809.server;

import io.github.cloudintheking.jt809.codec.MessageDecoder;
import io.github.cloudintheking.jt809.codec.MessageEncoder;
import io.github.cloudintheking.jt809.server.handler.AuthHandler;
import io.github.cloudintheking.jt809.server.handler.BusinessHandler;
import io.github.cloudintheking.jt809.server.handler.IMIdleStateHandler;
import io.github.cloudintheking.jt809.server.handler.LoginRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class Jt809Server {

    @Value("${tcp.port:8080}")
    private Integer port;

    @PostConstruct
    public void init() throws Exception {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new IMIdleStateHandler());
                        channel.pipeline().addLast(new MessageDecoder());
                        channel.pipeline().addLast(MessageEncoder.INSTANCE);
                        channel.pipeline().addLast(LoginRequestHandler.INSTANCE);
                        channel.pipeline().addLast(AuthHandler.INSTANCE);
                        channel.pipeline().addLast(BusinessHandler.INSTANCE);

                    }
                });
        bootstrap.bind().addListener(future -> {
            if (future.isSuccess()) {
                log.info("jt808server端口:{}绑定成功", port);
            } else {
                log.error("jt808server端口:{}绑定失败", port);
            }
        });

    }
}
