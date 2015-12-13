package com.wordpress.ilyaps.serverHelpers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 27.11.15.
 */
public final class GameContext {
    @NotNull
    private static final GameContext INSTANCE = new GameContext();

    @NotNull
    private final Map<Class<?>, Object> context = new HashMap<>();

    private GameContext() {
    }

    @NotNull
    public static GameContext getInstance() {
        return INSTANCE;
    }

    public void add(Class<?> clazz, Object object) {
        if (context.containsKey(clazz)) {
            throw new RuntimeException();
        }
        context.put(clazz, object);
    }

    @NotNull
    public Object get(Class<?> clazz) {
        Object obj = context.get(clazz);
        if (obj == null) {
            throw new RuntimeException();
        }
        return obj;
    }
}
