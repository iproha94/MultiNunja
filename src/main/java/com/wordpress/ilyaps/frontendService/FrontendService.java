package com.wordpress.ilyaps.frontendService;

import com.wordpress.ilyaps.messageSystem.Abonent;

/**
 * @author e.shubin
 */
public interface FrontendService extends Abonent , Runnable {
    void register(String name, String email, String password);

    boolean endedRegistration(String email);

    boolean successfulRegistration(String email);

    void registered(String name, boolean result);

    boolean alreadyRegistered(String name, String email);

    void authorization(String email, String password);

    boolean isAuthorized(String sessionId);

    void leaving(String sessionId);

    boolean isLeft(String sessionId);


}
