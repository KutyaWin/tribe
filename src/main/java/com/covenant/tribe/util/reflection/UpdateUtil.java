package com.covenant.tribe.util.reflection;

import com.covenant.tribe.exeption.util.IllegalAccessDuringUpdateException;

import java.lang.reflect.Field;

public class UpdateUtil {
    public static <T> void updateEntity(T a, T b) {
        try {
            if (a == null || b == null) return;
            for (Field field : a.getClass().getDeclaredFields()) {
                boolean accessibleValue = field.canAccess(a);
                field.setAccessible(true);
                Object otherValue = field.get(b);
                if (field.isAnnotationPresent(MyNested.class)) {
                    return;
                } else {
                    if (otherValue != null && !field.get(a).equals(otherValue))
                        field.set(a, otherValue);
                    field.setAccessible(accessibleValue);
                }
            }
        }  catch (IllegalAccessException e) {
            throw new IllegalAccessDuringUpdateException(e.getMessage());
        }
    }
}
