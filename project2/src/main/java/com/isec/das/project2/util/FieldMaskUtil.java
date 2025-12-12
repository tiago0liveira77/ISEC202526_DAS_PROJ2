package com.isec.das.project2.util; // Package declaration for utility classes

import org.springframework.util.ReflectionUtils; // Import ReflectionUtils

import java.lang.reflect.Field; // Import Field class
import java.util.HashMap; // Import HashMap
import java.util.Map; // Import Map

public class FieldMaskUtil { // Utility class for field masking

    public static Map<String, Object> applyFieldMask(Object object, String fields) { // Method to apply field mask to an object
        Map<String, Object> result = new HashMap<>(); // Create a map to store the result
        if (fields == null || fields.isEmpty()) { // Check if fields parameter is null or empty
            // Return all fields if no mask is provided - simplified for now, 
            // ideally we'd iterate all fields. For this demo, we'll rely on the caller 
            // or return the object itself if the framework handles it, but here we want a map.
            // Let's just return a map of all fields.
            Field[] allFields = object.getClass().getDeclaredFields(); // Get all declared fields of the object's class
            for (Field field : allFields) { // Iterate over all fields
                field.setAccessible(true); // Make the field accessible
                try { // Start try block
                    result.put(field.getName(), field.get(object)); // Put field name and value into the map
                } catch (IllegalAccessException e) { // Catch IllegalAccessException
                    // ignore
                }
            }
            return result; // Return the map with all fields
        }

        String[] fieldNames = fields.split(","); // Split the fields string by comma
        for (String fieldName : fieldNames) { // Iterate over each field name
            fieldName = fieldName.trim(); // Trim whitespace from field name
            Field field = ReflectionUtils.findField(object.getClass(), fieldName); // Find the field in the object's class
            if (field != null) { // Check if field exists
                field.setAccessible(true); // Make the field accessible
                try { // Start try block
                    result.put(fieldName, field.get(object)); // Put field name and value into the map
                } catch (IllegalAccessException e) { // Catch IllegalAccessException
                    // ignore
                }
            }
        }
        return result; // Return the map with selected fields
    }
}
