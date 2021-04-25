package io.github.cloudintheking.jt809.utils;

import java.text.NumberFormat;

public class ByteUtil {
    /**
     * 16进制字符串转换成byte数组
     *
     * @param hex
     */
    public static byte[] hexStringToByte(String hex) {
        hex = hex.toUpperCase();
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 补全位数不够的定长参数
     *
     * @param length
     * @param pwdByte
     */
    public static byte[] getBytesWithLengthAfter(int length, byte[] pwdByte) {
        byte[] lengthByte = new byte[length];
        for (int i = 0; i < pwdByte.length; i++) {
            lengthByte[i] = pwdByte[i];
        }
        for (int i = 0; i < (length - pwdByte.length); i++) {
            lengthByte[pwdByte.length + i] = 0x00;
        }
        return lengthByte;
    }

    /**
     * 格式化经纬度,保留六位小数
     *
     * @param needFormat
     */
    public static int formatLonLat(Double needFormat) {
        NumberFormat numFormat = NumberFormat.getInstance();
        numFormat.setMaximumFractionDigits(6);
        numFormat.setGroupingUsed(false);
        String firstFormat = numFormat.format(needFormat);
        Double formatDouble = Double.parseDouble(firstFormat);
        numFormat.setMaximumFractionDigits(0);
        String formatValue = numFormat.format(formatDouble * 1000000);
        return Integer.parseInt(formatValue);
    }
}
