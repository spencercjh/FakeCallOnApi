package spencercjh.top.fastrun.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static java.security.MessageDigest.getInstance;

/**
 * @author spencercjh
 */
public class Json2Package {
    private final static String SALT = "lpKK*TJE8WaIg%93O0pfn0#xS0i3xE$z";

    static public String getSign(String json) {
        try {
            return myMd5Encode(SALT + "data" + json);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    static public String getData(String json) {
        try {
            return URLEncoder.encode(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "error";
        }
    }

    private static String myMd5Encode(String inputStr) throws Exception {
        MessageDigest md5 = getInstance("MD5");
        byte[] bytes = inputStr.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = md5.digest(bytes);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int value = ((int) md5Byte) & 0xff;
            if (value < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(value));
        }
        return hexValue.toString();
    }
}
