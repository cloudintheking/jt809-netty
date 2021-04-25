# jt809 netty-springboot实现

## 实现

只采用了主链路通信

- 登陆请求/应答
- 心跳检测
- 车辆实时定位信息上传

### jt809协议注意点

- jt809应当根据头尾标识进行粘包/半包处理,详见[here](src/main/java/io/github/cloudintheking/jt809/codec/MessageDecoder.java)
- jt809由头标识+数据头+数据体+crc+尾标识, 针对数据头+数据体进行crc校验, 针对数据头+数据体+crc进行转义(去除数据中和头尾标识相同的数据,防止边界模糊)
- server 应该只负责处理解析数据,将解析后的数据通过消息队列或其他方式发给业务模块处理,防止慢io堵塞整个server

jt809协议详见 [here](doc/JTT%20809.pdf)

## server 运行

idea 打开项目后运行 Jt809Application

## client运行

test目录下执行TcpClientDemo进行测试
