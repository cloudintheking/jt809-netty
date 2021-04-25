package io.github.cloudintheking.jt809.protocol;

import io.github.cloudintheking.jt809.constants.MsgIdConstant;
import io.github.cloudintheking.jt809.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

/**
 * 车辆实时定位信息类
 */
@Data
public class UpExgMsgRealLocationEntity implements Serializable {

    /**
     * 年月日，时分秒
     */
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int sec;

    /**
     * 下级平台接入码，上级平台给下级平台分配的唯一标识
     */
    private long msgGnsscenterId;

    /**
     * 车牌号
     */
    private String vehicleNo = "";

    /**
     * 车牌颜色，按照JT/T 415-2006中5.4.12的规定
     */
    private byte vehicleColor;

    /**
     * 子业务类型标识
     */
    private int dataType;

    /**
     * 后续数据长度
     */
    private long dataLength;


    /**
     * 该字段标识传输的定位信息是否使用国家测绘局批准的地图保密插件进行加密
     * 机密标识：1 已加密 0 未加密
     */
    private int encrypt;

    /**
     * 上报时间
     */
    private Date dateTime;

    /**
     * 经度，单位为1*10^(-6)
     */
    private String lon = "";

    /**
     * 纬度，单位为1*10^(-6)
     */
    private String lat = "";

    /**
     * 速度，指卫星定位车载终端设备上传的行车速度信息，为必填项，单位为千米每小时（km/h）
     */
    private int vec1;

    /**
     * 行驶记录速度,指车辆行驶记录设备上传的行车速度信息，单位为千米每小时（km/h）
     */
    private int vec2;

    /**
     * 车辆当前总里程数，指车辆上传的行车里程数，单位为千米（km）
     */
    private long vec3;

    /**
     * 方向，0~359,单位为度（°），正北为0，顺时针
     */
    private int direction;

    /**
     * 海拔高度，单位为米（m）
     */
    private int altitude;

    /**
     * 车辆状态，二进制表示:B31B30... ...B2B1B0。
     * 具体定义按照JT/T 808-2011 中表17的规定
     */
    private long state;

    /**
     * 报警状态，二进制表示，0表示正常，1表示报警:B31B30B29... ...B2B1B0。
     * 具体定义按照JT/T 808-2011中表18的规定。
     */
    private long alarm;


    public ByteBuf toByteBuf() {
        ByteBuf buffer = Unpooled.buffer(64);//数据体总长
        buffer.writeBytes(ByteUtil.getBytesWithLengthAfter(21, vehicleNo.getBytes(Charset.forName("GBK"))));//21
        buffer.writeByte(1);//1
        buffer.writeShort(MsgIdConstant.UP_EXG_MSG_REAL_LOCATION);//2
        buffer.writeInt(36);//4
        //是否加密
        buffer.writeByte((byte) 0);//0未加密 // 1
        //日月年dmyy
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        buffer.writeByte((byte) cal.get(Calendar.DATE));
        buffer.writeByte((byte) (cal.get(Calendar.MONTH) + 1));
        String hexYear = "0" + Integer.toHexString(cal.get(Calendar.YEAR));
        buffer.writeBytes(ByteUtil.hexStringToByte(hexYear));//4

        //时分秒
        buffer.writeByte((byte) cal.get(Calendar.HOUR_OF_DAY));
        buffer.writeByte((byte) cal.get(Calendar.MINUTE));
        buffer.writeByte((byte) cal.get(Calendar.SECOND));//3
        //经度，纬度
        buffer.writeInt(ByteUtil.formatLonLat(Double.valueOf(lon)));//4
        buffer.writeInt(ByteUtil.formatLonLat(Double.valueOf(lat)));//4
        //速度
        buffer.writeShort(vec1);//2
        //行驶记录速度
        buffer.writeShort(vec2);//2
        //车辆当前总里程数
        buffer.writeInt((int) vec3);//4
        //方向
        buffer.writeShort(direction);//2
        //海拔
        buffer.writeShort((short) 0);//2
        //车辆状态
        int accStatus = 0;
        int gpsStatus = 0;
        if (accStatus == 0 && gpsStatus == 0) {
            buffer.writeInt(0);//4
        } else if (accStatus == 1 && gpsStatus == 0) {
            buffer.writeInt(1);//4
        } else if (accStatus == 0 && gpsStatus == 1) {
            buffer.writeInt(2);//4
        } else {
            buffer.writeInt(3);//4
        }
        //报警状态
        buffer.writeInt(1);//0表示正常；1表示报警//4
        return buffer;
    }

//    /**
//     * 接口开发商
//     */
//    private String developer;

//    /**
//     * 原始报文
//     */
//    private Blob receiveData;

}
