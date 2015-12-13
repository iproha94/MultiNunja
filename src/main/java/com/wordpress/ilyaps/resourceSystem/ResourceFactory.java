package com.wordpress.ilyaps.resourceSystem;

import com.wordpress.ilyaps.utils.SaxHandler;
import com.wordpress.ilyaps.utils.VirtualFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ilya on 01.11.15.
 */
public final class ResourceFactory {
    @SuppressWarnings("ConstantConditions")
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(ResourceFactory.class);

    @NotNull
    private SAXParser saxParser;

    @NotNull
    private static final ResourceFactory RESOURCEFACTORY = new ResourceFactory();

    private ResourceFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        if (factory == null) {
            LOGGER.error("factory == null");
            throw new NullPointerException();
        }
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException ignored) {
            LOGGER.error("ParserConfigurationException | SAXException");
            throw new RuntimeException();
        }
    }

    @NotNull
    public Resource get(@NotNull String xmlFile) {
        SaxHandler handler = new SaxHandler();

        try {
            saxParser.parse(xmlFile, handler);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        if (handler.getObject() == null) {
            LOGGER.error("handler.getObject() == null");
            throw new NullPointerException();
        }

        return (Resource) handler.getObject();
    }

    @NotNull
    public static Map<Class<?>, Resource> loadResources(@NotNull String resourcesDirectory) {
        Map<Class<?>, Resource> resources = new HashMap<>();
        VirtualFS virtualFS = new VirtualFS();
        Iterator<String> iter = virtualFS.getIterator(resourcesDirectory);
        while (iter.hasNext()) {
            String fileName = iter.next();
            LOGGER.info(fileName);
            if (fileName != null && fileName.contains(".xml")) {
                Resource resource = RESOURCEFACTORY.get(fileName);
                if (resource.getClass() != null) {
                    resources.put(resource.getClass(), resource);
                } else {
                    LOGGER.error("Fail to create resource class");
                }
            }
        }
        return resources;
    }
}
