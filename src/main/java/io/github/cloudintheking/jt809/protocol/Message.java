package io.github.cloudintheking.jt809.protocol;

import io.github.cloudintheking.jt809.utils.WrapAtomicInteger;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.Serializable;

/**
 * jt809消息协议
 */
@Data
public class Message implements Serializable {
    public static final int MSG_HEAD = 0x5b; //头标识
    public static final int MSG_TALL = 0x5d; //尾标识

    //报文中除数据体外，固定的数据长度(去掉头尾)
    public static final int MSG_FIX_LENGTH = 24;

    //消息头
    private static WrapAtomicInteger internalMsgNo = new WrapAtomicInteger(0);
    private long msgLength; //数据长度
    private int msgId; //业务类型
    private int msgSn; //消息序列号
    private long msgGnsscenterId; //接入码
    private byte[] versionFlag = {0, 0, 1}; //版本号
    private int encryptFlag = 1; //加密标志
    private long encryptKey = 2000; //加密密钥
    private int crcCode; //crc码

    //消息体
    private ByteBuf msgBody;

    public Message() {
    }


    public Message(int msgId) {
        this.msgSn = internalMsgNo.incrementAndGet();
        this.msgId = msgId;
    }


}
