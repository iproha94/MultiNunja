package com.wordpress.ilyaps.gamemechService;

import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.frontendService.message.MsgFrnSendData;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class GamemechServiceImpl1 implements GamemechService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GamemechServiceImpl1.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;

    public GamemechServiceImpl1(@NotNull MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
        messageSystem.addService(this);
        messageSystem.getAddressService().registerGamemechService(this);
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
    public void receiveData(String name, String data) {
        // тут приняли данные от сокета и как нить их обрабатываем
    }

    @Override
    public void sendData(String name, String data) {
        Message msg = new MsgFrnSendData(
                getAddress(),
                messageSystem.getAddressService().getFrontendServiceAddress(),
                name,
                data
        );
        messageSystem.sendMessage(msg);
    }

    @Override
    public void addUser(@NotNull String name) {
        LOGGER.info("пытается добавиться пользователь " + name);
    }

    @Override
    public boolean removeUser(@NotNull String name) {
        LOGGER.info("пытается уйти пользователь " + name);

        return false;
    }
}
