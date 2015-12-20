package com.wordpress.ilyaps.messageSystem;

import org.jetbrains.annotations.NotNull;

/**
 * @author e.shubin
 */
public abstract class Message {
    @NotNull
    private final Address from;
    @NotNull
    private final Address to;

    public Message(@NotNull Address from,@NotNull  Address to) {
        this.from = from;
        this.to = to;
    }

    @NotNull
    public Address getFrom() {
        return from;
    }

    @NotNull
    public Address getTo() {
        return to;
    }

    public abstract void exec(Abonent abonent);
}
