package com.wordpress.ilyaps.messageSystem;

import org.jetbrains.annotations.NotNull;

/**
 * @author e.shubin
 */
public interface Abonent {
    @NotNull
    Address getAddress();
    void sendMessage(Message msg);
}
