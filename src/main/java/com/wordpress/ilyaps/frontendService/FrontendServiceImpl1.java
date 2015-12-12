package com.wordpress.ilyaps.frontendService;

import com.wordpress.ilyaps.ThreadSettings;
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
    private final Map<String, Boolean> waitingUsers = new HashMap<>();


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
    }

    @Override
    public boolean endedRegistration(String email) {
        Boolean value = waitingUsers.get(email);
        if (value == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean successfulRegistration(String email) {
        return waitingUsers.remove(email);
    }

    @Override
    public void registered(String email, boolean result) {
        waitingUsers.put(email, result);
    }

    @Override
    public boolean alreadyRegistered(String name, String email) {

        return false;
    }

    @Override
    public void authorization(String email, String password) {

    }

    @Override
    public boolean isAuthorized(String sessionId) {

        return false;
    }

    @Override
    public void leaving(String sessionId) {

    }

    @Override
    public boolean isLeft(String sessionId) {
        return false;
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
