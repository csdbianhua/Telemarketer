package com.telemarket.telemarketer.http.requests;

import java.util.Collections;
import java.util.Map;

/**
 * Be careful!
 * Created by hason on 15/9/29.
 */
class RequestBody {
    private Map<String, String> formMap;
    private Map<String, MIMEData> mimeMap;


    RequestBody() {
        this.formMap = Collections.emptyMap();
        this.mimeMap = Collections.emptyMap();
    }

    public void setFormMap(Map<String, String> formMap) {
        this.formMap = formMap;
    }

    public void setMimeMap(Map<String, MIMEData> mimeMap) {
        this.mimeMap = mimeMap;
    }

    public boolean formContainKey(String key) {
        return formMap.containsKey(key);
    }

    public boolean mimeContainKey(String key) {
        return mimeMap.containsKey(key);
    }

    public Map<String, String> getFormMap() {
        return Collections.unmodifiableMap(formMap);
    }

    public Map<String, MIMEData> getMimeMap() {
        return Collections.unmodifiableMap(mimeMap);
    }

    public String formValue(String key) {
        return formMap.get(key);
    }

    public MIMEData mimeValue(String key) {
        return mimeMap.get(key);
    }
}
