package io.github.cloudintheking.jt809.client;

import io.github.cloudintheking.jt809.client.handler.HeartBeatHandler;
import io.github.cloudintheking.jt809.client.handler.LoginResponseHandler;
import io.github.cloudintheking.jt809.codec.MessageDecoder;
import io.github.cloudintheking.jt809.codec.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


@Data
public class TcpClient809 {
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private static final int DEFAULT_PORT = 9000;

    private long connectTimeoutMillis = 3000;

    private int port = DEFAULT_PORT;

    private boolean tcpNoDelay = false;

    private boolean reuseAddress = true;

    private boolean keepAlive = true;

    private int workerCount = 4;

    public static TcpClient809 INSTANCE = new TcpClient809();

    public Channel getChannel(String address, int port) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeoutMillis)
                .option(ChannelOption.SO_REUSEADDR, reuseAddress)
                .option(ChannelOption.SO_KEEPALIVE, keepAlive)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 64 * 1024)
                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 32 * 1024)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.ERROR));
                        socketChannel.pipeline().addLast(new HeartBeatHandler());//心跳发送包处理handler
                        socketChannel.pipeline().addLast(new MessageDecoder());//解码
                        socketChannel.pipeline().addLast(MessageEncoder.INSTANCE);//编码
                        socketChannel.pipeline().addLast(new LoginResponseHandler());//反馈数据处理
                    }
                });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(address, port));
        future.awaitUninterruptibly();
        if (future.isSuccess()) {
            return future.channel();
        } else {
            return null;
        }
    }
}
