package spencercjh.top.fastrun.common.util;

import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户名验证工具类
 *
 * @author Exrickx
 */
@SuppressWarnings("Annotator")
@Log4j2
public class UsernameUtil {

    /**
     * 由字母数字下划线组成且开头必须是字母，不能超过16位
     */
    private static final Pattern USERNAME = Pattern.compile("[a-zA-Z]{1}[a-zA-Z0-9_]{1,15}");

    /**
     * 手机号
     */
    private static final Pattern MOBILE = Pattern.compile("^1[3|4|5|8][0-9]\\d{8}$");

    /**
     * 邮箱
     */
    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");

    public static boolean username(String v) {

        Matcher m = USERNAME.matcher(v);
        return m.matches();
    }

    public static boolean mobile(String v) {

        Matcher m = MOBILE.matcher(v);
        return m.matches();
    }

    public static boolean email(String v) {

        Matcher m = EMAIL.matcher(v);
        return m.matches();
    }
}
