package com.wordpress.ilyaps.frontendService;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.accountService.message.MsgAccAuthorization;
import com.wordpress.ilyaps.accountService.message.MsgAccLeaving;
import com.wordpress.ilyaps.accountService.message.MsgAccRegister;
import com.wordpress.ilyaps.frontendSockets.WebSocketService;
import com.wordpress.ilyaps.gamemechService.message.MsgGmmCloseSocket;
import com.wordpress.ilyaps.gamemechService.message.MsgGmmOpenSocket;
import com.wordpress.ilyaps.gamemechService.message.MsgGmmReceiveData;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 12.12.15.
 */
public class FrontendServiceImpl implements FrontendService {

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(FrontendServiceImpl.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;

    @NotNull
    private final Map<String, UserProfile> sessionToProfile = new HashMap<>();
    @NotNull
    private final Map<String, UserState> emailToState = new HashMap<>();

    @Override
    public void registerUser(String name, String email, String password) {
        UserState state = checkState(email);
        if (state == UserState.SUCCESSFUL_REGISTERED ||
                state == UserState.SUCCESSFUL_AUTHORIZED) {
            LOGGER.warn("user " + email + " with status = " + state + " wants to register");
            return;
        }

        final Message msg = new MsgAccRegister(
                this.getAddress(),
                messageSystem.getAddressService().getAccountServiceAddress(),
                name,
                email,
                password
        );
        this.sendMessage(msg);
        emailToState.put(email, UserState.PENDING_REGISTRATION);
    }

    @Override
    public void registered(String email, UserProfile result) {
        if (result == null) {
            emailToState.put(email, UserState.UNSUCCESSFUL_REGISTERED);
        } else {
            emailToState.put(email, UserState.SUCCESSFUL_REGISTERED);
        }
    }

    @Override
    public void authorizationUser(String email, String password, String sessionId) {
        Message msg = new MsgAccAuthorization(
                getAddress(),
                messageSystem.getAddressService().getAccountServiceAddress(),
                sessionId,
                email,
                password
        );
        messageSystem.sendMessage(msg);
        emailToState.put(email, UserState.PENDING_AUTHORIZATION);
    }

    @Override
    public void authorized(String email, String sessionId, UserProfile result) {
        if (result == null) {
            emailToState.put(email, UserState.UNSUCCESSFUL_AUTHORIZED);
        } else {
            emailToState.put(email, UserState.SUCCESSFUL_AUTHORIZED);
            sessionToProfile.put(sessionId, result);
        }
    }

    @Override
    public void leaveUser(String email, String sessionId) {
        Message msg = new MsgAccLeaving(
                getAddress(),
                messageSystem.getAddressService().getAccountServiceAddress(),
                sessionId,
                email
        );
        messageSystem.sendMessage(msg);
        emailToState.put(email, UserState.PENDING_LEAVING);
    }

    @Override
    public void left(String email, String sessionId, UserProfile result) {
        if (result == null) {
            emailToState.put(email, UserState.UNSUCCESSFUL_LEFT);
        } else {
            emailToState.put(email, UserState.LEFT);
        }
    }

    @Override
    @NotNull
    public UserState checkState(String email) {
        if (emailToState.get(email) == null) {
            emailToState.put(email, UserState.LEFT);
        }
        return emailToState.get(email);
    }
    @Override
    @Nullable
    public UserProfile getUser(String sessionId) {
        return sessionToProfile.get(sessionId);
    }
    //-------------------------------------
    @NotNull
    private final WebSocketService webSocketService;

    public FrontendServiceImpl() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerFrontendService(this);

        this.webSocketService = (WebSocketService) gameContext.get(WebSocketService.class);
    }

    //---------------------------------------


    @Override
    public void openSocket(String name) {
        Message msg = new MsgGmmOpenSocket(
                getAddress(),
                messageSystem.getAddressService().getGamemechServiceAddress(),
                name
        );
        messageSystem.sendMessage(msg);
    }

    @Override
    public void closeSocket(String name) {
        Message msg = new MsgGmmCloseSocket(
                getAddress(),
                messageSystem.getAddressService().getGamemechServiceAddress(),
                name
        );
        messageSystem.sendMessage(msg);
    }

    //---------------------------------------

    @Override
    public void receiveData(String name, String data) {
        Message msg = new MsgGmmReceiveData(
                getAddress(),
                messageSystem.getAddressService().getGamemechServiceAddress(),
                name,
                data
        );
        messageSystem.sendMessage(msg);
    }

    @Override
    public void sendData(String name, String data) {
        webSocketService.notify(name, data);
    }

    //---------------------------------------

    @NotNull
    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void sendMessage(Message msg) {
        messageSystem.sendMessage(msg);
    }

    @Override
    public void run() {
        LOGGER.info("старт потока");

        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(ThreadSettings.SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("засыпания потока");
                LOGGER.error(e);
            }
        }
    }

}
