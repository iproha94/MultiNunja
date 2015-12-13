package com.wordpress.ilyaps.frontendService;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.accountService.message.MsgAccAuthorization;
import com.wordpress.ilyaps.accountService.message.MsgAccLeaving;
import com.wordpress.ilyaps.accountService.message.MsgAccRegister;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 12.12.15.
 */
public class FrontendServiceImpl1 implements FrontendService {

    @NotNull
    static final Logger LOGGER = LogManager.getLogger(FrontendServiceImpl1.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;

    @NotNull
    private final Map<String, UserProfile> waitingRegUsers = new HashMap<>();
    @NotNull
    private final Map<String, Boolean> readyRegistration = new HashMap<>();
    @NotNull
    private final Map<String, UserProfile> waitingAuthUsers = new HashMap<>();
    @NotNull
    private final Map<String, Boolean> readyAutherization = new HashMap<>();
    @NotNull
    private final Map<String, UserProfile> waitingLeftUsers = new HashMap<>();
    @NotNull
    private final Map<String, Boolean> readyLeaving = new HashMap<>();

    public FrontendServiceImpl1(@NotNull MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
        messageSystem.addService(this);
        messageSystem.getAddressService().registerFrontendService(this);
    }

    @Override
    public void register(String name, String email, String password) {
        final Message msg = new MsgAccRegister(
                this.getAddress(),
                messageSystem.getAddressService().getAccountServiceAddress(),
                name,
                email,
                password
        );
        this.sendMessage(msg);
        readyRegistration.put(email, false);
    }

    @Override
    public boolean endedRegistration(String email) {
        return readyRegistration.get(email);
    }

    @Override
    public UserProfile successfulRegistration(String email) {
        UserProfile profile = waitingRegUsers.get(email);
        readyRegistration.remove(email);
        waitingRegUsers.remove(email);
        return profile;
    }

    @Override
    public void registered(String email, UserProfile result) {
        readyRegistration.put(email, true);
        waitingRegUsers.put(email, result);
    }

    @Override
    public void authorization(String sessionId, String email, String password) {
        Message msg = new MsgAccAuthorization(
                getAddress(),
                messageSystem.getAddressService().getAccountServiceAddress(),
                sessionId,
                email,
                password
        );
        messageSystem.sendMessage(msg);
        readyAutherization.put(sessionId, false);
    }

    @Override
    public boolean endedAuthorization(String sessionId) {
        return readyAutherization.get(sessionId);
    }

    @Override
    public UserProfile successfulAuthorization(String sessionId) {
        UserProfile profile = waitingAuthUsers.get(sessionId);
        readyAutherization.remove(sessionId);
        waitingAuthUsers.remove(sessionId);
        return profile;
    }

    @Override
    public void authorized(String sessionId, UserProfile result) {
        readyAutherization.put(sessionId, true);
        waitingAuthUsers.put(sessionId, result);
    }

    @Override
    public void leaving(String sessionId) {
        Message msg = new MsgAccLeaving(
                getAddress(),
                messageSystem.getAddressService().getAccountServiceAddress(),
                sessionId
        );
        messageSystem.sendMessage(msg);
        readyLeaving.put(sessionId, false);
    }

    @Override
    public boolean endedLeaving(String sessionId) {
        return readyLeaving.get(sessionId);
    }

    @Override
    public UserProfile successfulLeaving(String sessionId) {
        UserProfile profile = waitingLeftUsers.get(sessionId);
        readyLeaving.remove(sessionId);
        waitingLeftUsers.remove(sessionId);
        return profile;
    }

    @Override
    public void left(String sessionId, UserProfile result) {
        readyLeaving.put(sessionId, true);
        waitingLeftUsers.put(sessionId, result);
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
