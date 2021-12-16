package android.util;

/*
This class is used to mock android logger for Unit test, given
they don't have access to android API
 */

@SuppressWarnings({"SameReturnValue", "unused"})
public class Log {
    public static int d(String tag, String msg) {
        System.out.printf("DEBUG: %s: %s%n",tag, msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.printf("INFO: %s: %s%n",tag, msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.printf("WARN: %s: %s%n",tag, msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.printf("ERROR: %s: %s%n",tag, msg);
        return 0;
    }

}