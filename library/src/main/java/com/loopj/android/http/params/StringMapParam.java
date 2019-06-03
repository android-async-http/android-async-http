package com.loopj.android.http.params;

import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.entity.ContentType;

public class StringMapParam extends BaseParam<Map<String, String>> {

    public StringMapParam(String name, Map<String, String> value, ContentType contentType) {
        super(name, value, contentType);
    }

    @Override
    public String getValue() {
        StringBuilder value = new StringBuilder();
        for (Map.Entry<String, String> s : getRawValue().entrySet()) {
            value.append(String.format(Locale.US, "%s[%s]=%s", getName(), s.getKey(), s.getValue()));
        }
        return value.toString();
    }
}
