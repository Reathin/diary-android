package com.rair.diary.utils;

import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mzaiy on 2017/6/2.
 */

public class RairUtils {

    /**
     * 隐藏输入法
     *
     * @param context 上下文
     */
    public static void hideInput(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 格式化时间
     *
     * @param value int 值
     * @return 01
     */
    public static String format(int value) {
        String s = String.valueOf(value);
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return 屏幕宽高[宽, 高]
     */
    public static int[] screenWH(Context context) {
        int[] screenWH = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWH[0] = size.x;
        screenWH[1] = size.y;
        return screenWH;
    }

    /**
     * 抖动动画
     *
     * @param CycleTimes 动画重复的次数
     * @return 动画
     */
    public static Animation shakeAnimation(int CycleTimes) {
        Animation translateAnimation = new TranslateAnimation(0, 6, 0, 6);
        translateAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    /**
     * 显示SnackBar
     *
     * @param view   视图
     * @param string 文本
     */
    public static void showSnackar(View view, String string) {
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 校验邮箱
     *
     * @param email 邮箱
     * @return 是否正确
     */
    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 验证手机号
     *
     * @param mobiles 手机号
     * @return true, false
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("[1][3578]\\d{9}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 随机生成字符串(nonce)
     *
     * @return 随机字符串
     */
    public static String randomStr(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";   //生成字符串从此序列中取
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {//32位
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取时间
     *
     * @return yyyy-MM-dd HH-mm-ss
     */
    public static String getTime() {
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return dateFormat.format(timeMillis);
    }
}
