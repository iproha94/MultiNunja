package com.wordpress.ilyaps.accountService;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 12.12.15.
 */
public class AccountServiceImpl1 implements AccountService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(AccountServiceImpl1.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;
    @NotNull
    AccountServiceDAO accountServiceDAO;

    public AccountServiceImpl1(@NotNull MessageSystem messageSystem, @NotNull AccountServiceDAO accountServiceDAO) {
        this.messageSystem = messageSystem;
        messageSystem.addService(this);
        messageSystem.getAddressService().registerAccountService(this);

        this.accountServiceDAO = accountServiceDAO;
    }

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

    @Override
    public AccountServiceDAO getAccountServiceDAO() {
        return accountServiceDAO;
    }
}
