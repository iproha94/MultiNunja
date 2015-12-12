package com.wordpress.ilyaps.messageSystem;

/**
 * @author e.shubin
 */
public interface Abonent {
    Address getAddress();
    void sendMessage(Message msg);
}
