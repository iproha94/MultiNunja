package com.wordpress.ilyaps.frontendService;

import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.messageSystem.Abonent;

/**
 * @author e.shubin
 */
public interface FrontendService extends Abonent, Runnable {


    void registered(String name, UserProfile result);

    void registerUser(String name, String email, String password);

    void authorized(String email, String sessionId, UserProfile result);

    void authorizationUser(String email, String password, String sessionId);

    void left(String email, String sessionId, UserProfile result);

    void leaveUser(String email, String sessionId);

    UserState checkState(String email);

    UserProfile getUser(String sessionId);


    //------------------------


    void openSocket(String name);

    void closeSocket(String name);


    void receiveData(String name, String data);

    void sendData(String name, String data);

}
