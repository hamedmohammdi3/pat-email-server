package com.fanap.fanrp.pat.patemailserver.utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Mehrdad Dehnamaki
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    static {
        init();
    }

    private static void init() {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        SimpleModule customModule = new SimpleModule();
        customModule.addSerializer(BigDecimal.class, new ToStringSerializer());
        customModule.addDeserializer(BigDecimal.class, new FromStringDeserializer<BigDecimal>(BigDecimal.class) {
            @Override
            protected BigDecimal _deserialize(String s, DeserializationContext deserializationContext) {
                return new BigDecimal(s);
            }
        });

        customModule.addDeserializer(Timestamp.class, new FromStringDeserializer<Timestamp>(Timestamp.class) {
            private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

            @Override
            protected Timestamp _deserialize(String value, DeserializationContext ctxt) {
                Date date = null;
                try {
                    date = new SimpleDateFormat(DATE_FORMAT).parse(value);
                } catch (Exception ignore) {
                }

                if (date == null) {
                    return new Timestamp(Long.parseLong(value));
                } else {
                    return new Timestamp(date.getTime());
                }
            }
        });
        MAPPER.registerModules(customModule);
    }

    public static String write(Object o) {
        if (o == null) return null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            MAPPER.writeValue(bos, o);
            bos.flush();
            return new String(bos.toByteArray(), DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static <O> O read(String s, Class<O> targetClass) {
        try {
            return MAPPER.readValue(s.getBytes(DEFAULT_CHARSET), targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    public static <O> O read(String s, JavaType javaType) {
        try {
            return (O) MAPPER.readValue(s.getBytes(DEFAULT_CHARSET), javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValid(String json) {
        if (json == null || json.isEmpty()) return false;
        try {
            MAPPER.readTree(json);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }


    public static class TypeUtil {
        private static final TypeFactory typeFactory = MAPPER.getTypeFactory();

        public static final JavaType BOOLEAN = getJavaType(Boolean.class);
        public static final JavaType CHAR = getJavaType(Character.class);
        public static final JavaType BYTE = getJavaType(Byte.class);
        public static final JavaType SHORT = getJavaType(Short.class);
        public static final JavaType INT = getJavaType(Integer.class);
        public static final JavaType LONG = getJavaType(Long.class);
        public static final JavaType FLOAT = getJavaType(Float.class);
        public static final JavaType DOUBLE = getJavaType(Double.class);
        public static final JavaType STRING = getJavaType(String.class);
        public static final JavaType OBJECT = getJavaType(Object.class);
        public static final ArrayType ARRAY = getArrayType(Object.class);
        public static final CollectionType ARRAY_LIST = getArrayListType(Object.class);
        public static final CollectionType SET = getSetType(Object.class);
        public static final MapType HASH_MAP = getHashMapType(Object.class);

        public static JavaType getJavaType(Class<?> clazz) {
            return typeFactory.constructType(clazz);
        }

        public static ArrayType getArrayType(Class<?> clazz) {
            return typeFactory.constructArrayType(clazz);
        }

        public static ArrayType getArrayType(JavaType javaType) {
            return typeFactory.constructArrayType(javaType);
        }

        public static CollectionType getArrayListType(Class<?> clazz) {
            return typeFactory.constructCollectionType(ArrayList.class, clazz);
        }

        public static CollectionType getArrayListType(JavaType javaType) {
            return typeFactory.constructCollectionType(ArrayList.class, javaType);
        }

        public static CollectionType getSetType(Class<?> clazz) {
            return typeFactory.constructCollectionType(Set.class, clazz);
        }

        public static CollectionType getSetType(JavaType javaType) {
            return typeFactory.constructCollectionType(Set.class, javaType);
        }

        public static <C extends List> CollectionType getCollectionType(Class<C> collectionClass, Class<?> clazz) {
            return typeFactory.constructCollectionType(collectionClass, clazz);
        }

        public static <C extends List> CollectionType getCollectionType(Class<C> collectionClass, JavaType javaType) {
            return typeFactory.constructCollectionType(collectionClass, javaType);
        }

        public static MapType getHashMapType(Class keyValueClass) {
            return typeFactory.constructMapType(HashMap.class, keyValueClass, keyValueClass);
        }

        public static MapType getHashMapType(Class keyClass, Class valueClass) {
            return typeFactory.constructMapType(HashMap.class, keyClass, valueClass);
        }

        public static MapType getHashMapType(JavaType keyType, JavaType valueType) {
            return typeFactory.constructMapType(HashMap.class, keyType, valueType);
        }

        public static <M extends Map> MapType getMapType(Class<M> mapType, Class keyClass, Class valueClass) {
            return typeFactory.constructMapType(mapType, keyClass, valueClass);
        }

        public static <M extends Map> MapType getMapType(Class<M> mapType, JavaType keyType, JavaType valueType) {
            return typeFactory.constructMapType(mapType, keyType, valueType);
        }

    }

}
