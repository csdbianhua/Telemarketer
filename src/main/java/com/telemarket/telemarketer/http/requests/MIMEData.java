package com.telemarket.telemarketer.http.requests;

/**
 * Be careful!
 * Created by hason on 15/9/29.
 */
public class MIMEData {

    private String type;
    private byte[] data;
    private String fileName;

    public MIMEData(String type, byte[] data, String fileName) {
        this.type = type;
        this.data = data;
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }
}
