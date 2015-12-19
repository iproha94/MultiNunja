package com.wordpress.ilyaps.services.servletsService;

import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.messageSystem.Abonent;

/**
 * @author e.shubin
 */
public interface ServletsService extends Abonent, Runnable {


    void registered(String name, UserProfile result);

    void registerUser(String name, String email, String password);

    void authorized(String email, String sessionId, UserProfile result);

    void authorizationUser(String email, String password, String sessionId);

    void left(String email, String sessionId, UserProfile result);

    void leaveUser(String email, String sessionId);

    UserState checkUserState(String email);

    UserProfile getUser(String sessionId);

}
