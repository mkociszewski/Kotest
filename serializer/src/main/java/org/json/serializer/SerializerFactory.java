package org.json.serializer;


public class SerializerFactory {

    public static JsonSerializer createSerializer() {
        //TODO
        return new JsonSerializer() {
            @Override
            public <T> T fromJson(String json, Class<T> type) {
                throw new IllegalStateException("Not implemented");
            }

            @Override
            public String toJson(Object object) {
                throw new IllegalStateException("Not implemented");
            }
        };
    }

}
