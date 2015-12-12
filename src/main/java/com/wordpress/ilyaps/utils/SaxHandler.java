package com.wordpress.ilyaps.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by ilya on 01.11.15.
 */
public class SaxHandler extends DefaultHandler {
    @SuppressWarnings("ConstantConditions")
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(SaxHandler.class);

    private String element = null;
    private Object object = null;

    @Override
    public void startDocument() throws SAXException {
        LOGGER.info("Start document");
    }

    @Override
    public void endDocument() throws SAXException {
        LOGGER.info("End document ");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!"class".equals(qName)) {
            element = qName;
        } else {
            if (attributes == null) {
                LOGGER.error("attributes == null");
                return;
            }
            String className = attributes.getValue(0);
            if (className == null) {
                LOGGER.error("className == null");
                return;
            }
            LOGGER.info("Class name: {}", className);
            object = ReflectionHelper.createInstance(className);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        element = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (element != null) {
            if (ch == null || object == null) {
                LOGGER.error("ch == null or object == null");
                return;
            }

            String value = new String(ch, start, length);
            LOGGER.info("{} = {}", element, value);
            ReflectionHelper.setFieldValue(object, element, value);
        }
    }

    @Nullable
    public Object getObject() {
        return object;
    }
}