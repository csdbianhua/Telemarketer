package com.telemarket.telemarketer.context;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/13
 */
public class ScanConfig {
    private String homePath;
    private String packageName;

    public ScanConfig(String homePath, String packageName) {
        this.homePath = homePath;
        this.packageName = packageName;
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
