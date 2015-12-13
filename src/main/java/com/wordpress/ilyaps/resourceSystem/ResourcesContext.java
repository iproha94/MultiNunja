package com.wordpress.ilyaps.resourceSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by ilya on 27.11.15.
 */
public class ResourcesContext {
    @SuppressWarnings("ConstantConditions")
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(ResourcesContext.class);

    private Map<Class<?>, Resource> context;

    private ResourcesContext() {
    }

    public ResourcesContext(@NotNull String dir) {
        context = ResourceFactory.loadResources(dir);
    }

    public void add(Class<?> clazz, Resource resource) {
        if (context == null) {
            LOGGER.error("context == null");
            throw new NullPointerException();
        }

        if (context.containsKey(clazz)) {
            throw new RuntimeException();
        }
        context.put(clazz, resource);
    }

    @NotNull
    public Resource get(Class<?> clazz) {
        if (context == null) {
            LOGGER.error("context == null");
            throw new NullPointerException();
        }

        Resource resource =  context.get(clazz);
        if (resource == null) {
            throw new NullPointerException();
        }
        return resource;
    }
}
