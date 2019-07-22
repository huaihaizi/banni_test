package com.hanwin.product.common;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.Date;

/**
 *
 * @author zhaopf
 * @date 2018/6/24 0024
 */

public class DateNullAdapterFactory<T> implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (rawType != Date.class) {
            return null;
        }
        return (TypeAdapter<T>) new DateNullAdapter();
    }
}
