package com.telemarket.telemarketer.context;

/**
 * Chen Yijie on 2016/11/27 21:08.
 */
public class Context {
    private static String bashPath;
    private static String packageName;

    static void setBashPath(String bashPath) {
        Context.bashPath = bashPath;
    }

    static void setPackageName(String packageName) {
        Context.packageName = packageName;
    }

    public static String getBashPath() {
        return bashPath;
    }


    public static String getPackageName() {
        return packageName;
    }

}
