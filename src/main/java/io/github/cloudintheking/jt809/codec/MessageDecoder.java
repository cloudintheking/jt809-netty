package io.github.cloudintheking.jt809.codec;

import io.github.cloudintheking.jt809.protocol.Message;
import io.github.cloudintheking.jt809.utils.EncryptUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * jt809数据包解码
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //根据头、尾标识拆包
        int beginIndex = byteBuf.readerIndex();
        Byte head = byteBuf.readByte();
        if (head.intValue() != Message.MSG_HEAD) {
            return;
        }
        Byte tail = byteBuf.readByte();
        while (byteBuf.isReadable() && tail.intValue() != Message.MSG_TALL) {
            tail = byteBuf.readByte();
        }
        if (tail.intValue() != Message.MSG_TALL) {
            byteBuf.readerIndex(beginIndex);
            return;
        }
        int endIndex = byteBuf.readerIndex();
        ByteBuf messageBuff = byteBuf.slice(beginIndex, endIndex - beginIndex);
        //转换为message对象
        Message message = buildMessage(messageBuff);
        list.add(message);
    }


    public Message buildMessage(ByteBuf content) throws Exception {
        byte[] contentBytes = new byte[content.capacity()];
        content.getBytes(0, contentBytes);
        //将所有字节转义
        byte[] formatBytes = doEscape4Receive(contentBytes, 0, content.writerIndex() - content.readerIndex());
        //将转移后字节赋给content
        content = Unpooled.copiedBuffer(formatBytes);

        Message message = new Message();
        //1 byte 头5b
        content.skipBytes(1);
        //4 byte 数据长度
        message.setMsgLength(content.readUnsignedInt());
        //4 byte  报文序列号
        message.setMsgSn(content.readInt());
        //2 byte  主业务类型
        message.setMsgId(content.readUnsignedShort());
        //4 byte 下级平台接入码
        message.setMsgGnsscenterId(content.readUnsignedInt());
        //3 byte 版本号 v x.x.x
        ByteBuf versionFlagByteBuf = content.readBytes(3);
        byte[] versionFlagBytes = new byte[3];
        versionFlagByteBuf.getBytes(0, versionFlagBytes);
        message.setVersionFlag(versionFlagBytes);
        //1 byte 是否加密 0 不加密 1 加密
        message.setEncryptFlag(content.readUnsignedByte());
        //4 byte 数据加密钥
        message.setEncryptKey(content.readUnsignedInt());
        //数据体为变长字节 去掉校验码与尾标识的数据体
        ByteBuf bodyByteBuf = content.readBytes(content.readableBytes() - 2 - 1);
        byte[] bodyBytes = new byte[bodyByteBuf.capacity()];
        bodyByteBuf.getBytes(0, bodyBytes);
        //解密
        if (message.getEncryptFlag() == 1L) {
            bodyBytes = EncryptUtil.encrypt('A', 'B', 'C', message.getEncryptKey(), bodyBytes);
        }
        message.setMsgBody(Unpooled.buffer(bodyBytes.length).writeBytes(bodyBytes));
        //校验码
        message.setCrcCode(content.readUnsignedShort());
        byte[] b = new byte[bodyBytes.length + 22];
        content.getBytes(1, b);
        int crcValue = EncryptUtil.crc16(b);
        if ((crcValue & 0xffff) != message.getCrcCode()) {
            log.error("接入码:{},crc校验失败", message.getMsgGnsscenterId());
        }
        //跳过尾标识
        content.skipBytes(1);
        return message;
    }


    /**
     * 接收消息时转义<br>
     *
     * <pre>
     * 0x5A01 <====> 0x5B
     * 0x5A02 <====> 0x5A
     * 0x5E01 <====> 0x5D
     * 0x5E02 <====> 0x5E
     * </pre>
     *
     * @param bs    要转义的字节数组
     * @param start 起始索引
     * @param end   结束索引
     * @return 转义后的字节数组
     * @throws Exception
     */
    public byte[] doEscape4Receive(byte[] bs, int start, int end) throws Exception {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (int i = 0; i < start; i++) {
                baos.write(bs[i]);
            }
            for (int i = start; i < end - 1; i++) {
                if (bs[i] == 0x5A && bs[i + 1] == 0x01) {
                    baos.write(0x5B);
                    i++;
                } else if (bs[i] == 0x5A && bs[i + 1] == 0x02) {
                    baos.write(0x5A);
                    i++;
                } else if (bs[i] == 0x5E && bs[i + 1] == 0x01) {
                    baos.write(0x5D);
                    i++;
                } else if (bs[i] == 0x5E && bs[i + 1] == 0x02) {
                    baos.write(0x5E);
                    i++;
                } else {
                    baos.write(bs[i]);
                }
            }
            for (int i = end - 1; i < bs.length; i++) {
                baos.write(bs[i]);
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


    /**
     * Returns the index within the given input string of the first occurrence
     * of the specified substring.
     */
    public static int getFirstMatchingIndex(byte[] bytes, byte query) {
        int inputLength = bytes.length;
        int inputIndex = 0;
        int index = -1;
        while (inputIndex < inputLength) {
            if (bytes[inputIndex] == query) {
                return inputIndex;
            } else {
                inputIndex++;
            }
        }
        return index;
    }

}
