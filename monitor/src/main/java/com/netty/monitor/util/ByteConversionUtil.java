package com.netty.monitor.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: DAI
 * @date: Created in 2020/10/21 14:19
 * @description：
 */
public class ByteConversionUtil {

    /**
     * 将字节数组转换成一个整数
     */
    public static int byte2Int(byte[] bytes) {
        if (bytes.length < 4) {
            if (bytes.length != 0) {
                byte[] bytes1 = new byte[4];
                if (bytes.length == 3) {
                    bytes1[0] = 0x00;
                    bytes1[1] = bytes[0];
                    bytes1[2] = bytes[1];
                    bytes1[3] = bytes[2];
                } else if (bytes.length == 2) {
                    bytes1[0] = 0x00;
                    bytes1[1] = 0x00;
                    bytes1[2] = bytes[0];
                    bytes1[3] = bytes[1];
                } else if (bytes.length == 1) {
                    bytes1[0] = 0x00;
                    bytes1[1] = 0x00;
                    bytes1[2] = 0x00;
                    bytes1[3] = bytes[0];
                }
                return (bytes1[0] & 0xff) << 24
                        | (bytes1[1] & 0xff) << 16
                        | (bytes1[2] & 0xff) << 8
                        | (bytes1[3] & 0xff);
            } else {
                return 0;
            }
        }
        return (bytes[0] & 0xff) << 24
                | (bytes[1] & 0xff) << 16
                | (bytes[2] & 0xff) << 8
                | (bytes[3] & 0xff);
    }

    /**
     * 将十六进制的字符串转换成字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStrToBinaryStr(String hexString) {

        if (hexString == null || "".equals(hexString)) {
            return null;
        }

        hexString = hexString.replaceAll(" ", "");

        int len = hexString.length();
        int index = 0;

        byte[] bytes = new byte[len / 2];

        while (index < len) {

            String sub = hexString.substring(index, index + 2);

            bytes[index / 2] = (byte) Integer.parseInt(sub, 16);

            index += 2;
        }

        return bytes;
    }


    //将指定byte数组以16进制返回
    public static String printHexString(byte[] b, Integer type) {
        String temp = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            if (type == 1) {
                temp += hex + ",";
            } else {
                temp += hex;
            }

        }
        return temp;
    }


    /**
     * 将指定String数组以10进制返回
     */
    public static List<Long> printStringHx(List<String> list) {
        List<Long> listRes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            long l = Long.parseLong(list.get(i), 16);
            listRes.add(l);
        }
        return listRes;

    }
}
