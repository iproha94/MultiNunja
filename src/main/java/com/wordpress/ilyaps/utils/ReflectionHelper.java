package com.wordpress.ilyaps.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Created by ilya on 01.11.15.
 */
public class ReflectionHelper {
    @SuppressWarnings("ConstantConditions")
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(ReflectionHelper.class);

    @NotNull
    public static Object createInstance(@NotNull String className) {
        Object obj;
        try {
            //noinspection ConstantConditions
            obj = Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
            LOGGER.error(ignored);
            throw new RuntimeException();
        }

        if (obj == null) {
            LOGGER.error("obj == null");
            throw new NullPointerException();
        }
        return obj;
    }

    public static void setFieldValue(@NotNull Object object,
                                     @NotNull String fieldName,
                                     @NotNull String value) {
        Field field;
        try {
            //noinspection ConstantConditions
            field = object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            LOGGER.error(e);
            return;
        }

        if (field == null) {
            LOGGER.error("field == null");
            return;
        }

        field.setAccessible(true);
        try {

            //noinspection ConstantConditions
            if (field.getType().equals(String.class)) {
                field.set(object, value);

            } else if (field.getType().equals(int.class)) {
                field.set(object, Integer.decode(value));
            }

        } catch (IllegalAccessException ignored) {
            LOGGER.error(ignored);
            throw new RuntimeException();
        }

        field.setAccessible(false);
    }
}
