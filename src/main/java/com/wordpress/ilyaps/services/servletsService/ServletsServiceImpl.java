package com.wordpress.ilyaps.services.servletsService;

import com.wordpress.ilyaps.services.ThreadSettings;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.services.accountService.message.MsgAccAuthorization;
import com.wordpress.ilyaps.services.accountService.message.MsgAccLeaving;
import com.wordpress.ilyaps.services.accountService.message.MsgAccRegister;
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
public class ServletsServiceImpl implements ServletsService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(ServletsServiceImpl.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;

    @NotNull
    private final Map<String, UserProfile> sessionToProfile = new HashMap<>();
    @NotNull
    private final Map<String, UserState> emailToState = new HashMap<>();

    public ServletsServiceImpl() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerServletsService(this);
    }

    @Override
    public void registerUser(@NotNull String name, @NotNull String email, @NotNull String password) {
        UserState state = getUserState(email);
        if (state == UserState.SUCCESSFUL_REGISTERED ||
                state == UserState.SUCCESSFUL_AUTHORIZED) {
            LOGGER.warn("user " + email + " with status = " + state + " wants to register");
            return;
        }

        final Message msg = new MsgAccRegister(
                address,
                messageSystem.getAddressService().getAccountServiceAddress(),
                name,
                email,
                password
        );
        this.sendMessage(msg);
        emailToState.put(email, UserState.PENDING_REGISTRATION);
    }

    @Override
    public void registered(@NotNull String email, UserProfile result) {
        if (result == null) {
            emailToState.put(email, UserState.UNSUCCESSFUL_REGISTERED);
        } else {
            emailToState.put(email, UserState.SUCCESSFUL_REGISTERED);
        }
    }

    @Override
    public void authorizationUser(@NotNull String email, @NotNull String password, @NotNull String sessionId) {
        Message msg = new MsgAccAuthorization(
                address,
                messageSystem.getAddressService().getAccountServiceAddress(),
                sessionId,
                email,
                password
        );
        messageSystem.sendMessage(msg);
        emailToState.put(email, UserState.PENDING_AUTHORIZATION);
    }

    @Override
    public void authorized(@NotNull String email, @NotNull String sessionId, UserProfile result) {
        if (result == null) {
            emailToState.put(email, UserState.UNSUCCESSFUL_AUTHORIZED);
        } else {
            emailToState.put(email, UserState.SUCCESSFUL_AUTHORIZED);
            sessionToProfile.put(sessionId, result);
        }
    }

    @Override
    public void leaveUser(@NotNull String email, @NotNull String sessionId) {
        Message msg = new MsgAccLeaving(
                address,
                messageSystem.getAddressService().getAccountServiceAddress(),
                sessionId,
                email
        );
        messageSystem.sendMessage(msg);
        emailToState.put(email, UserState.PENDING_LEAVING);

    }

    @Override
    public void left(@NotNull String email, @NotNull String sessionId, UserProfile result) {
        if (result == null) {
            emailToState.put(email, UserState.UNSUCCESSFUL_LEFT);
        } else {
            emailToState.put(email, UserState.SUCCESSFUL_LEFT);
        }
    }
    @Override
    @NotNull
    public UserState getUserState(@NotNull String email) {
        if (emailToState.get(email) == null) {
            emailToState.put(email, UserState.SUCCESSFUL_LEFT);
        }
        return emailToState.get(email);
    }
    @Override
    @Nullable
    public UserProfile getUserProfile(@NotNull String sessionId) {
        return sessionToProfile.get(sessionId);
    }

    @Override
    public void removeUserProfile(@NotNull String sessionId) {
        sessionToProfile.remove(sessionId);
    }

    @Override
    public void removeUserState(@NotNull String email) {
        emailToState.put(email, UserState.SUCCESSFUL_LEFT);
    }

    @Override
    public void addSession(@NotNull String sessionId, @NotNull UserProfile profile) {
        sessionToProfile.put(sessionId, profile);
    }

    @Override
    public void clearAll() {
        emailToState.clear();
        sessionToProfile.clear();
    }

    @Override
    public void addState(String email, UserState state) {
        emailToState.put(email, state);
    }

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
        LOGGER.info("start thread");

        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(ThreadSettings.SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("sleep thread");
                LOGGER.error(e);
            }
        }
    }

}
