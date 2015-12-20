package com.wordpress.ilyaps.messageSystem;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author e.shubin
 */
public final class Address {
    @NotNull
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
    private final int id;

    public Address(){
        id = ID_GENERATOR.getAndIncrement();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return id == address.id;

    }
}
