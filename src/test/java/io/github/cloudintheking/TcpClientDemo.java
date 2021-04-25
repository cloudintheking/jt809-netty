package io.github.cloudintheking;


import io.github.cloudintheking.jt809.client.TcpClient809;
import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.protocol.Message;
import io.github.cloudintheking.jt809.protocol.UpExgMsgRealLocationEntity;
import io.github.cloudintheking.jt809.utils.ByteUtil;
import io.github.cloudintheking.jt809.utils.SessionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpClientDemo {
    /**
     * 交委指定本公司接入码
     */
    public static int PLANT_CODE = 0x00;
    /**
     * 交委指定本公司用户名
     */
    public static int COM_ID = 0x11;
    /**
     * 交委指定本公司密码
     */
    public static String COM_PWD = "123456";

    private static int LOGIN_FLAG = 1;

    private static String DOWN_LINK_IP = "127.0.0.1";

    private Channel channel;

    private static String SERVER_ADDRESS = "127.0.0.1";
    private static int SERVER_PORT = 8080;

    public TcpClientDemo(Channel channel) {
        this.channel = channel;

    }

    public static void main(String[] args) {
        try {
            Channel channel = TcpClient809.INSTANCE.getChannel(SERVER_ADDRESS, SERVER_PORT); //建立连接
            TcpClientDemo tcpClientDemo = new TcpClientDemo(channel);//初始测试对象
            tcpClientDemo.login();//登陆
            Thread.sleep(5 * 1000); //等5秒
            //创建实时定位对象
            UpExgMsgRealLocationEntity realLocationEntity = new UpExgMsgRealLocationEntity();
            realLocationEntity.setDirection(120);
            realLocationEntity.setLon("117.2900911");
            realLocationEntity.setLat("39.56362");
            realLocationEntity.setVec1(45);
            realLocationEntity.setAlarm(10001L);
            realLocationEntity.setVehicleNo("测试车牌号xxxx");
            tcpClientDemo.sendExgRealLocationMsg(realLocationEntity);//发送实时定位信息

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 登录jt809server
     */
    public void login() {
        if (!SessionUtil.hasLogin(channel)) {
            //构建登陆消息
            Message msg = new Message(MsgIdConstant.UP_CONNECT_REQ);
            msg.setMsgGnsscenterId(PLANT_CODE);
            ByteBuf buffer = Unpooled.buffer(46);
            buffer.writeInt(COM_ID);//4

            byte[] pwd = ByteUtil.getBytesWithLengthAfter(8, COM_PWD.getBytes());
            buffer.writeBytes(pwd);//8

            byte[] ip = ByteUtil.getBytesWithLengthAfter(32, DOWN_LINK_IP.getBytes());
            buffer.writeBytes(ip);//32
            buffer.writeShort((short) 1);//2
            msg.setMsgBody(buffer);
            channel.writeAndFlush(msg);
            log.info("第{}次登陆", LOGIN_FLAG++);
        }
    }

    /**
     * 发送实时定位数据到jt809server
     *
     * @param realLocationEntity
     */
    public void sendExgRealLocationMsg(UpExgMsgRealLocationEntity realLocationEntity) {
        //连接是否存在
        if (channel != null && channel.isWritable()) {
            //是否登陆
            if (SessionUtil.hasLogin(channel)) {
                Message msg = new Message(MsgIdConstant.UP_EXG_MSG);
                msg.setMsgBody(realLocationEntity.toByteBuf());
                channel.writeAndFlush(msg);
                log.info("发送实时定位消息包:{}", realLocationEntity.toString());
            } else {
                login();//重登录
            }
        } else {
            channel = TcpClient809.INSTANCE.getChannel(SERVER_ADDRESS, SERVER_PORT);//重建连接
        }

    }

}