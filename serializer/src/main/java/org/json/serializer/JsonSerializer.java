package org.json.serializer;

public interface JsonSerializer {

    <T> T fromJson(String json, Class<T> type);

    String toJson(Object object);

}
