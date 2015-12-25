package com.wordpress.ilyaps.services.socketsService;

import com.wordpress.ilyaps.services.ThreadSettings;
import com.wordpress.ilyaps.frontendSockets.GameWebSocket;
import com.wordpress.ilyaps.services.gamemechService.message.MsgGmmCloseSocket;
import com.wordpress.ilyaps.services.gamemechService.message.MsgGmmOpenSocket;
import com.wordpress.ilyaps.services.gamemechService.message.MsgGmmReceiveData;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ilya on 27.10.15.
 */
public class SocketsServiceImpl implements SocketsService {
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(SocketsServiceImpl.class);

    @NotNull
    private final Map<String, GameWebSocket> userSockets = new ConcurrentHashMap<>();


    @Override
    public void openSocket(@NotNull String name) {
        Message msg = new MsgGmmOpenSocket(
                address,
                messageSystem.getAddressService().getGamemechServiceAddress(),
                name
        );
        messageSystem.sendMessage(msg);
    }

    @Override
    public void closeSocket(@NotNull String name) {
        Message msg = new MsgGmmCloseSocket(
                address,
                messageSystem.getAddressService().getGamemechServiceAddress(),
                name
        );
        messageSystem.sendMessage(msg);
    }

    @Override
    public void receiveData(@NotNull String name, @NotNull String data) {
        Message msg = new MsgGmmReceiveData(
                address,
                messageSystem.getAddressService().getGamemechServiceAddress(),
                name,
                data
        );
        messageSystem.sendMessage(msg);
    }

    @Override
    public void sendData(@NotNull String name, @NotNull String data) {
        GameWebSocket gameWebSocket = userSockets.get(name);
        if (gameWebSocket == null) {
            LOGGER.warn("gameWebSocket == null");
            return;
        }

        gameWebSocket.send(data);
    }


    @Override
    public void add(@NotNull GameWebSocket userSocket) {
        userSockets.put(userSocket.getMyName(), userSocket);
    }

    @Override
    public void remove(@NotNull String name) {
        userSockets.remove(name);
    }

    public SocketsServiceImpl() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerSocketsService(this);
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