package com.isec.das.project2.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class FieldMaskUtil {

    public static Map<String, Object> applyFieldMask(Object object, String fields) {
        if (object == null) {
            return null;
        }

        Set<String> fieldSet = new HashSet<>();
        if (fields != null && !fields.isEmpty()) {
            String[] split = fields.split(",");
            for (String f : split) {
                fieldSet.add(f.trim());
            }
        }

        return applyMaskInternal(object, fieldSet, "");
    }

    private static Map<String, Object> applyMaskInternal(Object object, Set<String> fieldSet, String prefix) {
        Map<String, Object> result = new HashMap<>();
        if (object == null) {
            return result;
        }

        Field[] allFields = object.getClass().getDeclaredFields();
        boolean includeAll = fieldSet.isEmpty() || fieldSet.contains("*") || fieldSet.contains(prefix + "*");

        for (Field field : allFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fullPath = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;

            if (includeAll || isFieldRequested(fullPath, fieldSet)) {
                try {
                    Object value = field.get(object);
                    if (value != null && !isPrimitiveOrWrapper(value.getClass()) && !(value instanceof String)) {
                        // Handle nested objects and collections
                        if (value instanceof Collection<?>) {
                            List<Object> listResult = new ArrayList<>();
                            for (Object item : (Collection<?>) value) {
                                if (isPrimitiveOrWrapper(item.getClass()) || item instanceof String) {
                                    listResult.add(item);
                                } else {
                                    listResult.add(applyMaskInternal(item, fieldSet, fullPath));
                                }
                            }
                            result.put(fieldName, listResult);
                        } else if (value instanceof Map<?, ?>) {
                            result.put(fieldName, value); // Maps are taken as is for simplicity or could be recursed
                        } else {
                            result.put(fieldName, applyMaskInternal(value, fieldSet, fullPath));
                        }
                    } else {
                        result.put(fieldName, value);
                    }
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }
        }
        return result;
    }

    private static boolean isFieldRequested(String fullPath, Set<String> fieldSet) {
        if (fieldSet.contains(fullPath))
            return true;
        // Check for wildcard parent
        // e.g. if fullPath is "libraries.name" and fieldSet has "libraries.*"
        String[] parts = fullPath.split("\\.");
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0)
                current.append(".");
            current.append(parts[i]);
            if (fieldSet.contains(current.toString() + ".*")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class;
    }
}
