package thermostats.base;

import thermostats.bridge.FirebaseBridge;
import thermostats.model.LogEventModel;

/**
 * Created by Tim on 2016-07-26.
 */
public class Log {

    public final static int FATAL_INT = 50000;
    public final static int ERROR_INT = 40000;
    public final static int WARN_INT  = 30000;
    public final static int INFO_INT  = 20000;
    public final static int DEBUG_INT = 10000;
    public static final int TRACE_INT = 5000;


    private static FirebaseBridge mBridge;

    public static void initialize(FirebaseBridge bridge) {
        mBridge = bridge;
    }

    public static void t(String tag, String message) {
        log(TRACE_INT, tag, message);
    }

    public static void d(String tag, String message) {
        log(DEBUG_INT, tag, message);
    }

    public static void i(String tag, String message) {
        log(INFO_INT, tag, message);
    }

    public static void w(String tag, String message) {
        log(WARN_INT, tag, message);
    }

    public static void e(String tag, String message) {
        log(ERROR_INT, tag, message);
    }

    public static void f(String tag, String message) {
        log(FATAL_INT, tag, message);
    }

    private static void log(int level, String tag, String message) {
        if(mBridge != null && tag != null && !tag.isEmpty() && message != null && !message.isEmpty()) {
            mBridge.publishLogCall(tag, new LogEventModel(System.currentTimeMillis(), level, message));
        }
    }
}
