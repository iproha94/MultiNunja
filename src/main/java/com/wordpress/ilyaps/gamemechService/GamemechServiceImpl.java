package com.wordpress.ilyaps.gamemechService;

import com.wordpress.ilyaps.accountService.message.MsgAccScore;
import com.wordpress.ilyaps.frontendService.message.MsgFrnSendData;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.resourceSystem.ResourcesContext;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    private GMResource gMResource;
    @NotNull
    private final Map<String, GameSession> nameToGame = new HashMap<>();
    @NotNull
    private final Set<GameSession> allSessions = new HashSet<>();
    @NotNull
    private final Set<String> namesPlayers = new HashSet<>();

    public GamemechServiceImpl() {
        GameContext gameContext = GameContext.getInstance();

        this.messageSystem = (MessageSystem) gameContext.get(MessageSystem.class);
        messageSystem.addService(this);
        messageSystem.getAddressService().registerGamemechService(this);

        ResourcesContext resourcesContext = (ResourcesContext) gameContext.get(ResourcesContext.class);
        this.gMResource = (GMResource) resourcesContext.get(GMResource.class);
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

        int stepTime = gMResource.getStepTime();
        int gameTime = gMResource.getGameTime();

        while (true) {
            messageSystem.execForAbonent(this);
            gmStep(gameTime);
            try {
                Thread.sleep(stepTime);
            } catch (InterruptedException e) {
                LOGGER.error("засыпания потока");
                LOGGER.error(e);
            }
        }
    }

    private void gmStep(int gameTime) {
        for (Iterator<GameSession> iterator = allSessions.iterator(); iterator.hasNext(); ) {
            GameSession session = iterator.next();
            if (session == null) {
                LOGGER.warn("session == null");
            } else if (session.getSessionTime() > gameTime) {
                finishGame(session);
                iterator.remove();
            }
        }
    }

    @Override
    public void sendData(String name, String data) {
        Message msg = new MsgFrnSendData(
                getAddress(),
                messageSystem.getAddressService().getFrontendServiceAddress(),
                name,
                data
        );
        this.sendMessage(msg);
    }

    @Override
    public void addUser(@NotNull String name) {
        namesPlayers.add(name);
        LOGGER.info(name + " вошел в игру");

        if (namesPlayers.size() == gMResource.getNumberPlayers()) {
            startGame();
            namesPlayers.clear();
        }
    }

    @Override
    public boolean removeUser(@NotNull String name) {
        LOGGER.info("пытается уйти пользователь " + name);
        if (namesPlayers.remove(name)) {
            return true;
        }

        GameSession gameSession = nameToGame.get(name);
        if (gameSession == null) {
            return false;
        }

        gameSession.removeGameUser(name);
        nameToGame.remove(name);

        if (gameSession.getGameUsers().isEmpty()) {
            allSessions.remove(gameSession);
            return true;
        }

        String message = GameMessageCreator.createMessageLeave(name);

        for (GameUser user : gameSession.getGameUsers()) {
            sendData(user.getName(), message);
        }

        return true;
    }

    private void startGame() {
        GameSession gameSession = new GameSession(namesPlayers);
        allSessions.add(gameSession);
        LOGGER.info("start game");

        for (String userName : namesPlayers) {
            nameToGame.put(userName, gameSession);
            GameUser gameUser = gameSession.getGameUser(userName);

            String message = GameMessageCreator.createMessageStartGame(gameSession, userName, gMResource.getGameTime());

            if (gameUser != null) {
                sendData(userName, message);

            } else {
                LOGGER.error("gameuser == null");
            }
        }
    }

    private void finishGame(@NotNull GameSession session) {
        String message = GameMessageCreator.createMessageGameOver(session);
        LOGGER.info("finish game");

        for (GameUser user : session.getGameUsers()) {
            sendData(user.getName(), message);

            Message msg = new MsgAccScore(
                    getAddress(),
                    messageSystem.getAddressService().getFrontendServiceAddress(),
                    user.getName(),
                    user.getScore()
            );
            this.sendMessage(msg);

            nameToGame.remove(user.getName());
        }
    }
}
