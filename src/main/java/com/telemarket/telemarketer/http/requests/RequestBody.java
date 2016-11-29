package com.telemarket.telemarketer.http.requests;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Be careful!
 * Created by hason on 15/9/29.
 */
class RequestBody {
    private MultiValuedMap<String, String> formMap;
    private Map<String, MimeData> mimeMap;


    public void setFormMap(MultiValuedMap<String, String> formMap) {
        this.formMap = formMap;
    }

    public void setMimeMap(Map<String, MimeData> mimeMap) {
        this.mimeMap = mimeMap;
    }

    public boolean formContainKey(String key) {
        return formMap.containsKey(key);
    }

    public boolean mimeContainKey(String key) {
        return mimeMap.containsKey(key);
    }

    public MultiValuedMap<String, String> getFormMap() {
        return formMap;
    }

    public Map<String, MimeData> getMimeMap() {
        return Collections.unmodifiableMap(mimeMap);
    }

    public Collection<String> formValue(String key) {
        return formMap.get(key);
    }

    public MimeData mimeValue(String key) {
        return mimeMap.get(key);
    }
}
