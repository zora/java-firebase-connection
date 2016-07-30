package thermostats.base;

import thermostats.bridge.FirebaseBridge;
import thermostats.model.LogEventModel;

/**
 * Created by Tim on 2016-07-26.
 */
public class Log {

    public enum LogLevel {
        TRACE(5000),
        DEBUG(10000),
        INFO(20000),
        WARN(30000),
        ERROR(40000),
        FATAL(50000);

        public final int value;

        LogLevel(int value) {
            this.value = value;
        }

        public static LogLevel fromIdentifier(int value) {
            for (LogLevel lvl : values()) {
                if (value == lvl.value) {
                    return lvl;
                }
            }
            return null;
        }
    }

    private static FirebaseBridge mBridge;
    private static LogLevel mLogLevel = LogLevel.DEBUG;
    public static final String DEFAULT_TAG = "main";

    public static void initialize(FirebaseBridge bridge) {
        mBridge = bridge;
    }

    public static void t(String tag, String message) {
        log(LogLevel.TRACE, tag, message);
    }

    public static void d(String tag, String message) {
        log(LogLevel.DEBUG, tag, message);
    }

    public static void i(String tag, String message) {
        log(LogLevel.INFO, tag, message);
    }

    public static void w(String tag, String message) {
        log(LogLevel.WARN, tag, message);
    }

    public static void e(String tag, String message) {
        log(LogLevel.ERROR, tag, message);
    }

    public static void f(String tag, String message) {
        log(LogLevel.FATAL, tag, message);
    }

    private static void log(LogLevel level, String tag, String message) {
        if (mBridge != null && tag != null && !tag.isEmpty() && message != null && !message.isEmpty()) {
            System.out.println(level.name() + ": " + tag + " - " + message);
            mBridge.publishLogCall(tag, new LogEventModel(System.currentTimeMillis(), level.value, message));
        }
    }
}
