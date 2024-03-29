package com.nec.lib.android.httprequest.net.transform;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class NullTypeAdapterFactory<T> implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType= (Class<T>) type.getRawType();
        if (rawType!=String.class) {
            return null;
        }
        return (TypeAdapter<T>) new NullAdapter();
    }
}
