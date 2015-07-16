package com.loopj.android.http;

/**
 * Interface independent to any library, which currently uses same interface as {@link android.util.Log} implementation
 * You can change currently used LogInterface through {@link AsyncHttpClient#setLogInterface(LogInterface)}
 */
public interface LogInterface {

    int VERBOSE = 2;
    int DEBUG = 3;
    int INFO = 4;
    int WARN = 5;
    int ERROR = 6;
    int WTF = 8;

    boolean isLoggingEnabled();

    void setLoggingEnabled(boolean loggingEnabled);

    int getLoggingLevel();

    void setLoggingLevel(int loggingLevel);

    boolean shouldLog(int logLevel);

    void v(String tag, String msg);

    void v(String tag, String msg, Throwable t);

    void d(String tag, String msg);

    void d(String tag, String msg, Throwable t);

    void i(String tag, String msg);

    void i(String tag, String msg, Throwable t);

    void w(String tag, String msg);

    void w(String tag, String msg, Throwable t);

    void e(String tag, String msg);

    void e(String tag, String msg, Throwable t);

    void wtf(String tag, String msg);

    void wtf(String tag, String msg, Throwable t);


}
