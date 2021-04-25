package io.github.cloudintheking.jt809.protocol.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResponse implements Serializable {
    /**
     * 验证结果，定义如下：
     * 0x00:成功;
     * 0x01:IP 地址不正确；
     * 0x02:接入码不正确；
     * 0x03:用户没用注册；
     * 0x04:密码错误;
     * 0x05:资源紧张，稍后再连接(已经占用）；
     * 0x06：其他
     */
    private int result;
    /**
     * 校验码
     */
    private int verifyCode;


    public ByteBuf toByteBuf() {
        ByteBuf resBuf = Unpooled.buffer(5);
        resBuf.writeByte(result);
        resBuf.writeInt(verifyCode);
        return resBuf;
    }
}
