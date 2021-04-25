package io.github.cloudintheking.jt809.service;

import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.protocol.Message;
import io.github.cloudintheking.jt809.protocol.UpExgMsgRealLocationEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 车辆动态信息交换报文处理
 */
@Service
@Slf4j
public class ExchangeMsgService implements BusinessService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, Message msg) {

        byte[] vehicleNoBytes = new byte[21];
        msg.getMsgBody().readBytes(vehicleNoBytes);
        String vehicleNo = new String(vehicleNoBytes, Charset.forName("GBK"));
        byte vehicleColor = msg.getMsgBody().readByte();
        int dataType = msg.getMsgBody().readUnsignedShort();
        long dataLength = msg.getMsgBody().readUnsignedInt();

        switch (dataType) {
            //实时车辆定位
            case MsgIdConstant.UP_EXG_MSG_REAL_LOCATION:
                ByteBuf childBody = msg.getMsgBody().readBytes((int) dataLength);
                //储存车辆实时定位信息
                upExgMsgRealLocation(channelHandlerContext, msg, vehicleNo, vehicleColor, dataType, dataLength, childBody);
                break;
            default:
                log.info("其他消息 :" + msg.toString());
        }
    }

    @Override
    public boolean support(int msgId) {
        return msgId == MsgIdConstant.UP_EXG_MSG;
    }

    /**
     * 储存车辆实时定位信息
     *
     * @param msg
     * @param vehicleNo
     * @param vehicleColor
     * @param dataType
     * @param dataLength
     * @param childBody
     */
    private void upExgMsgRealLocation(ChannelHandlerContext channelHandlerContext, Message msg, String vehicleNo, byte vehicleColor, int dataType, long dataLength, ByteBuf childBody) {
        byte encrypt = childBody.readByte();
        byte day = childBody.readByte();
        byte month = childBody.readByte();
        int year = childBody.readUnsignedShort();
        byte hour = childBody.readByte();
        byte min = childBody.readByte();
        byte sec = childBody.readByte();
        long lon = childBody.readUnsignedInt();
        String lonStr = String.valueOf(lon).substring(0, 3) + "." + String.valueOf(lon).substring(3);
        long lat = childBody.readUnsignedInt();
        String latStr = String.valueOf(lat).substring(0, 2) + "." + String.valueOf(lat).substring(2);
        int vec1 = childBody.readUnsignedShort();
        int vec2 = childBody.readUnsignedShort();
        long vec3 = childBody.readUnsignedInt();
        int direction = childBody.readUnsignedShort();
        int altttude = childBody.readUnsignedShort();
        long state = childBody.readUnsignedInt();
        long alarm = childBody.readUnsignedInt();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        UpExgMsgRealLocationEntity upExgMsgRealLocation = new UpExgMsgRealLocationEntity();
        upExgMsgRealLocation.setYear(year);
        upExgMsgRealLocation.setMonth(month);
        upExgMsgRealLocation.setDay(day);
        upExgMsgRealLocation.setHour(hour);
        upExgMsgRealLocation.setMin(min);
        upExgMsgRealLocation.setSec(sec);

        upExgMsgRealLocation.setVehicleNo(vehicleNo);
        upExgMsgRealLocation.setVehicleColor(vehicleColor);
        upExgMsgRealLocation.setDataType(dataType);
        upExgMsgRealLocation.setDataLength(dataLength);

        upExgMsgRealLocation.setEncrypt(encrypt);
        upExgMsgRealLocation.setDateTime(dateTime);
        upExgMsgRealLocation.setLon(lonStr);
        upExgMsgRealLocation.setLat(latStr);
        upExgMsgRealLocation.setVec1(vec1);
        upExgMsgRealLocation.setVec2(vec2);
        upExgMsgRealLocation.setVec3(vec3);
        upExgMsgRealLocation.setDirection(direction);
        upExgMsgRealLocation.setAltitude(altttude);
        upExgMsgRealLocation.setState(state);
        upExgMsgRealLocation.setAlarm(alarm);

        upExgMsgRealLocation.setMsgGnsscenterId(msg.getMsgGnsscenterId());

        log.info("实时上传车辆定位信息 " + upExgMsgRealLocation.toString());
        //消息应答报文回复接收成功
        Message msgRep = new Message(MsgIdConstant.UP_LINK_TEST_RSP);
        //发送定位信息到mq
        this.rabbitTemplate.convertAndSend("jt809", "jt809.exgMsg.realLocation", upExgMsgRealLocation, new CorrelationData(String.valueOf(msgRep.getMsgId())));
        ByteBuf buffer = Unpooled.buffer(0);
        msgRep.setMsgBody(buffer);
        channelHandlerContext.channel().write(msgRep);
    }

}
