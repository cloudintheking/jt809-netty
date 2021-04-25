package io.github.cloudintheking.jt809.protocol.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {
    /**
     * 下级平台用户名
     */
    private String userId;
    /**
     * 下级平台用户密码
     */
    private String password;
    /**
     * 下级平台服务端ip
     */
    private String ip;
    /**
     * 下级服务端端口
     */
    private String port;
}
