package io.github.cloudintheking.jt809.codec;

import io.github.cloudintheking.jt809.protocol.Message;
import io.github.cloudintheking.jt809.utils.EncryptUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;

/**
 * jt809数据包编码
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<Message> {


    public static MessageEncoder INSTANCE = new MessageEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message msg, ByteBuf finalBuffer) throws Exception {
        int bodyLength = msg.getMsgBody().capacity();
        ByteBuf buffer = Unpooled.buffer(bodyLength + Message.MSG_FIX_LENGTH);
        //--------------数据头----------
        buffer.writeInt(bodyLength + Message.MSG_FIX_LENGTH + 2);  //4 加头尾
        buffer.writeInt(msg.getMsgSn());     //4
        buffer.writeShort(msg.getMsgId());   //2
        buffer.writeInt(1); //4
        buffer.writeBytes(msg.getVersionFlag());//3
        buffer.writeByte(0);//1
        buffer.writeInt(20000000);//4
        //--------------数据体----------
        byte[] bodyBytes = new byte[bodyLength];
        //加密
        if (msg.getEncryptFlag() == 1L) {
            bodyBytes = EncryptUtil.encrypt('A', 'B', 'C', msg.getEncryptKey(), msg.getMsgBody().array());
        }
        buffer.writeBytes(Unpooled.copiedBuffer(bodyBytes));
        //------------crc校验码---------
        byte[] b = Unpooled.buffer(bodyLength + 22).array();
        buffer.getBytes(0, b);
        int crcValue = EncryptUtil.crc16(b);
        buffer.writeShort(crcValue & 0xffff);//2
        byte[] bytes = buffer.array();
        byte[] formatedBytes = null;
        try {
            formatedBytes = doEscape4Receive(bytes, 0, bytes.length);
        } catch (Exception e) {
            log.error("转义异常:{}", e);
        }

        finalBuffer.writeByte(Message.MSG_HEAD);//1
        finalBuffer.writeBytes(formatedBytes);
        finalBuffer.writeByte(Message.MSG_TALL);  //1
    }

    /**
     * 报文转义
     * void
     *
     * @param bs
     * @param
     */
    public byte[] doEscape4Receive(byte[] bs, int start, int end) throws Exception {
        if (start < 0 || end > bs.length) {
            throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        }
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (int i = start; i < end; i++) {
                if (bs[i] == 0x5B) {
                    baos.write(0x5A);
                    baos.write(0x01);
                } else if (bs[i] == 0x5A) {
                    baos.write(0x5A);
                    baos.write(0x02);
                } else if (bs[i] == 0x5D) {
                    baos.write(0x5E);
                    baos.write(0x01);
                } else if (bs[i] == 0x5E) {
                    baos.write(0x5E);
                    baos.write(0x02);
                } else {
                    baos.write(bs[i]);
                }
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (baos != null) {
                baos.close();
                baos = null;
            }
        }
    }
}
