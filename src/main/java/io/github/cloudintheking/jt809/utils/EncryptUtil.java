package io.github.cloudintheking.jt809.utils;

/**
 * 转16进制
 */
public class EncryptUtil {
    /**
     * 生成crc
     *
     * @param bytesArr
     * @return
     */
    public static int crc16(byte[]... bytesArr) {
        int b = 0;
        int crc = 0xffff;

        for (byte[] d : bytesArr) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < 8; j++) {
                    b = ((d[i] << j) & 0x80) ^ ((crc & 0x8000) >> 8);
                    crc <<= 1;
                    if (b != 0)
                        crc ^= 0x1021;
                }
            }
        }
        crc = ~crc;
        return crc;
    }

    /**
     * 加密，解密 执行第一次加密 第二次变回原值解密
     *
     * @param M1
     * @param IA1
     * @param IC1
     * @param key
     * @param data
     * @return
     */
    public static byte[] encrypt(int M1, int IA1, int IC1, long key, byte[] data) {
        if (data == null) return null;
        byte[] array = data;//使用原对象，返回原对象
        //byte[] array = new byte[data.length]; //数组复制 返回新的对象
        //System.arraycopy(data, 0, array, 0, data.length);
        int idx = 0;
        if (key == 0) {
            key = 1;
        }
        int mkey = M1;
        if (0 == mkey) {
            mkey = 1;
        }
        while (idx < array.length) {
            key = IA1 * (key % mkey) + IC1;
            array[idx] ^= ((key >> 20) & 0xFF);
            idx++;
        }
        return array;
    }
}
