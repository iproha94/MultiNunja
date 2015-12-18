package com.wordpress.ilyaps.services.socketsService;

import com.wordpress.ilyaps.ThreadSettings;
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
    private final Map<String, GameWebSocket> userSockets = new HashMap<>();


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
        notify(name, data);
    }


    @Override
    public void add(@NotNull GameWebSocket userSocket) {
        userSockets.put(userSocket.getMyName(), userSocket);
    }

    @Override
    public boolean remove(@NotNull String name) {
        return userSockets.remove(name) != null;
    }

    @Override
    public void notify(@NotNull String userName, @NotNull String message) {
        GameWebSocket gameWebSocket = userSockets.get(userName);
        if (gameWebSocket == null) {
            LOGGER.error("gameWebSocket == null");
            return;
        }

        gameWebSocket.send(message);
    }

    public SocketsServiceImpl() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerSocketsService(this);
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
}