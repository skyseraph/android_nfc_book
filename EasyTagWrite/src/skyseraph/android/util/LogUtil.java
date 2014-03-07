
package skyseraph.android.util;

/**
 * @Title ： LogUtil.java
 * @Package ： tcl.nfc.phone.util
 * @ClassName : LogUtil
 * @Description ： 通用调试类
 * @author ： skyseraph00@163.com
 * @date ： 2013-5-16 上午9:22:43
 * @version ： V1.2
 */
public class LogUtil {

    public static void v(String tag, String msg) {
        if (MyConstant.isVerbose) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (MyConstant.isVerbose) {
            android.util.Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        if (MyConstant.isDebug) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (MyConstant.isDebug) {
            android.util.Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (MyConstant.isInformation) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (MyConstant.isInformation) {
            android.util.Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (MyConstant.isWarning) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (MyConstant.isWarning) {
            android.util.Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (MyConstant.isError) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (MyConstant.isError) {
            android.util.Log.e(tag, msg, t);
        }
    }
}
