package com.wordpress.ilyaps.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ilya on 01.11.15.
 */
public class VirtualFS {
    private final String root;

    public VirtualFS() {
        this.root = "";
    }

    public boolean isDirectory(String path) {
        return new File(root + path).isDirectory();
    }

    @NotNull
    public Iterator<String> getIterator(@NotNull String startDir) {
        return new FileIterator(startDir);
    }

    private final class FileIterator implements Iterator<String> {
        @NotNull
        private final Queue<File> files = new LinkedList<>();

        private FileIterator(@NotNull String path) {
            File file = new File(root + path);
            files.add(file);
        }

        @Override
        public boolean hasNext() {
            return !files.isEmpty();
        }

        @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
        @Override
        public String next() {
            File file = files.peek();
            if (file != null && file.isDirectory()) {
                //noinspection ConstantConditions
                Collections.addAll(files, file.listFiles());
            }

            //noinspection ConstantConditions
            return files.poll().getAbsolutePath();
        }

        @Override
        public void remove() {

        }

    }

    @SuppressWarnings("unused")
    public String getAbsolutePath(String file) {
        return new File(root + file).getAbsolutePath();
    }

}
