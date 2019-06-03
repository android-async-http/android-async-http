package com.loopj.android.http.params;

import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.ContentType;

public class StringListParam extends BaseParam<List<String>> {

    public StringListParam(String name, List<String> value, ContentType contentType) {
        super(name, value, contentType);
    }

    @Override
    public String getValue() {
        StringBuilder value = new StringBuilder();
        int counter = 0;
        for (String s : getRawValue()) {
            value.append(String.format(Locale.US, "%s[%d]=%s", getName(), counter++, s));
        }
        return value.toString();
    }
}
