package com.wordpress.ilyaps.frontendService;

import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.messageSystem.Abonent;

/**
 * @author e.shubin
 */
public interface FrontendService extends Abonent , Runnable {
    void register(String name, String email, String password);

    boolean endedRegistration(String email);

    UserProfile successfulRegistration(String email);

    void registered(String name, UserProfile result);




    void authorization(String sessionId, String email, String password);

    boolean endedAuthorization(String sessionId);

    UserProfile successfulAuthorization(String sessionId);

    void authorized(String sessionId, UserProfile result);




    void leaving(String sessionId);

    boolean endedLeaving(String sessionId);

    UserProfile successfulLeaving(String sessionId);

    void left(String sessionId, UserProfile result);



    void gettingUserProfile(String sessionId);

    boolean endedGettingUserProfile(String sessionId);

    UserProfile successfulGettingUserProfile(String sessionId);

    void getUserProfile(String sessionId, UserProfile result);



    void openSocket(String name);

    void closeSocket(String name);



    void receiveData(String name, String data);

    void sendData(String name, String data);

}
