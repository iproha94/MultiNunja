package com.wordpress.ilyaps.services.gamemechService;

import com.wordpress.ilyaps.services.ThreadSettings;
import com.wordpress.ilyaps.services.accountService.message.MsgAccAddScore;
import com.wordpress.ilyaps.services.socketsService.message.MsgSckSendData;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.resourceSystem.ResourcesContext;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by ilya on 13.12.15.
 */
public abstract class GamemechServiceImpl implements GamemechService {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GamemechServiceImpl.class);
    @NotNull
    private final Address address = new Address();
    @NotNull
    private final MessageSystem messageSystem;
    @NotNull
    private final Map<String, GameSession> nameToGame = new ConcurrentHashMap<>();
    @NotNull
    private final Set<GameSession> allSessions = new CopyOnWriteArraySet<>();
    @NotNull
    private final Set<String> namesPlayers = new CopyOnWriteArraySet<>();
    final int stepTime;
    final int gameTime;
    final int maxPlayers;

    public GamemechServiceImpl() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerGamemechService(this);

        ResourcesContext resourcesContext = (ResourcesContext) gameContext.get(ResourcesContext.class);
        GamemechResource gamemechResource = (GamemechResource) resourcesContext.get(GamemechResource.class);

        stepTime = gamemechResource.getStepTime();
        gameTime = gamemechResource.getGameTime();
        maxPlayers = gamemechResource.getNumberPlayers();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::stepGame, 0, stepTime, TimeUnit.MILLISECONDS);
    }


    @Override
    @NotNull
    public Map<String, GameSession> getNameToGame() {
        return nameToGame;
    }

    @Override
    @NotNull
    public Set<GameSession> getAllSessions() {
        return allSessions;
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

    private void stepGame() {
        stepGameFinish();
        stepGameStart();
    }

    private void stepGameFinish() {
        for (GameSession session : allSessions) {
            if (session.getSessionTime() > gameTime) {
                finishGame(session);
                allSessions.remove(session);
            }
        }
    }

    private void stepGameStart() {
        List<String> listNames = new ArrayList<>(namesPlayers);

        while (listNames.size() >= maxPlayers) {
            Set<String> setNames = new HashSet<>(maxPlayers);

            for (int i = 0; i < maxPlayers; ++i) {
                setNames.add(listNames.get(i));
                namesPlayers.remove(listNames.get(i));
            }

            for (String name : setNames) {
                listNames.remove(name);
            }

            startGame(setNames);
        }
    }

        @Override
    public void sendData(@NotNull String name, @NotNull String data) {
        Message msg = new MsgSckSendData(
                address,
                messageSystem.getAddressService().getSocketsServiceAddress(),
                name,
                data
        );
        this.sendMessage(msg);
    }

    @Override
    public void addUser(@NotNull String name) {
        namesPlayers.add(name);
        LOGGER.info(name + " go in game");
    }

    @Override
    public void removeUser(@NotNull String name) {
        LOGGER.info(name + " go out game");
        if (namesPlayers.remove(name)) {
            return;
        }

        GameSession gameSession = nameToGame.get(name);
        if (gameSession == null) {
            return;
        }

        gameSession.removeGameUser(name);
        nameToGame.remove(name);

        if (gameSession.getGameUsers().isEmpty()) {
            allSessions.remove(gameSession);
            return;
        }

        String message = GameMessageCreator.createMessageLeave(name);

        for (GameUser user : gameSession.getGameUsers()) {
            sendData(user.getName(), message);
        }
    }

    private void startGame(Set<String> setNames) {
        GameSession gameSession = new GameSession(setNames);
        allSessions.add(gameSession);
        LOGGER.info("start game");

        for (GameUser user : gameSession.getGameUsers()) {
            nameToGame.put(user.getName(), gameSession);

            String message = GameMessageCreator.createMessageStartGame(gameSession, user.getName(), gameTime);
            sendData(user.getName(), message);
        }
    }

    private void finishGame(@NotNull GameSession session) {
        String message = GameMessageCreator.createMessageGameOver(session);
        LOGGER.info("finish game");

        for (GameUser user : session.getGameUsers()) {
            sendData(user.getName(), message);

            Message msg = new MsgAccAddScore(
                    address,
                    messageSystem.getAddressService().getAccountServiceAddress(),
                    user.getName(),
                    user.getScore()
            );
            this.sendMessage(msg);

            nameToGame.remove(user.getName());
        }
    }
}
