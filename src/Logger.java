public class Logger {

    public static final int DEBUG = 3, ERROR = 6, INFO = 4, VERBOSE = 2, WARN = 5;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());

    public static void log(final String msg) {
        final StackTraceElement stackTrace = new Exception().getStackTrace()[1];

        String fileName = stackTrace.getFileName();
        if (fileName == null) fileName = "";  // It is necessary if you want to use proguard obfuscation.

        final String info = "(" + fileName + ":"
                + stackTrace.getLineNumber() + ") ";

        log(info + msg, DEBUG, stackTrace.getMethodName());
    }

    public static void log(final String msg, final int type) {
        final StackTraceElement stackTrace = new Exception().getStackTrace()[1];

        String fileName = stackTrace.getFileName();
        if (fileName == null) fileName = "";  // It is necessary if you want to use proguard obfuscation.

        final String info = "(" + fileName + ":"
                + stackTrace.getLineNumber() + ") ";

        log(info + msg, type, stackTrace.getMethodName());
    }

    public static void log(final String msg, final String tag) {
        final StackTraceElement stackTrace = new Exception().getStackTrace()[1];

        String fileName = stackTrace.getFileName();
        if (fileName == null) fileName = "";  // It is necessary if you want to use proguard obfuscation.

        final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                + stackTrace.getLineNumber() + ")";

        log(info + ": " + msg, DEBUG, tag); // default is debug
    }

    public static void log(final String msg, final int type, final String tag) {
        final StackTraceElement stackTrace = new Exception().getStackTrace()[1];

        String fileName = stackTrace.getFileName();
        if (fileName == null) fileName = "";  // It is necessary if you want to use proguard obfuscation.

        final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                + stackTrace.getLineNumber() + ")";

        switch (type) {
            case DEBUG:
                System.out.printf("[DEBUG] [%s] %s%n", tag, msg);
                break;
            case ERROR:
                System.out.printf("[ERROR] [%s] %s%n", tag, msg);
                break;
            case INFO:
                System.out.printf("[INFO] [%s] %s%n", tag, msg);
                break;
            case VERBOSE:
                System.out.printf("[VERBOSE] [%s] %s%n", tag, msg);
                break;
            case WARN:
                System.out.printf("[WARN] [%s] %s%n", tag, msg);
                break;
        }
    }
}
