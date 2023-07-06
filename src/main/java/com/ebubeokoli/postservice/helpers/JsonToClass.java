package com.ebubeokoli.postservice.helpers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

public class JsonToClass<T> {
    
    public static boolean convertJsonToClass(JSONObject json, Class classToConvert, String[] nullableFields) {

        Field[] fields = classToConvert.getDeclaredFields();
        String[] clss = {String.class.getName(), int.class.getName(), Long.class.getName(), char.class.getName(), short.class.getName()};
        List<String> classes = Arrays.asList(clss);

        List<String> nullables = Arrays.asList(nullableFields);

        for (Field field : fields) {

            if (!classes.contains(field.getType().getName())) continue;

            if (!json.has(field.getName()) && !nullables.contains(field.getName())) {
                return false;
            }
        }

        return true;
    }

}
