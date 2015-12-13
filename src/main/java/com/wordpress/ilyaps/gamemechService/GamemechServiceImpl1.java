package com.wordpress.ilyaps.gamemechService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wordpress.ilyaps.ThreadSettings;
import com.wordpress.ilyaps.frontendService.message.MsgFrnSendData;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.messageSystem.MessageSystem;
import com.wordpress.ilyaps.resourceSystem.GMResource;
import com.wordpress.ilyaps.resourceSystem.ResourcesContext;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    @NotNull
    private GMResource gMResource;
    @NotNull
    private final GameMessageCreator gameMessageCreator = new GameMessageCreator(this);
    @NotNull
    private final Map<String, GameSession> nameToGame = new HashMap<>();
    @NotNull
    private final Set<GameSession> allSessions = new HashSet<>();
    @NotNull
    private final Set<String> namesPlayers = new HashSet<>();

    public GamemechServiceImpl1(@NotNull MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
        messageSystem.addService(this);
        messageSystem.getAddressService().registerGamemechService(this);

        GameContext gameContext = GameContext.getInstance();
        ResourcesContext resourcesContext = (ResourcesContext) gameContext.get(ResourcesContext.class);
        this.gMResource = (GMResource) resourcesContext.get(GMResource.class);
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
                Thread.sleep(ThreadSettings.SLEEP_TIME);
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
    public void receiveData(String name, String data) {
        // тут приняли данные от сокета и как нить их обрабатываем
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObj = jsonParser.parse(data).getAsJsonObject();

        String status = jsonObj.get("status").getAsString();

        if ("increment".equals(status)) {
            incrementScore(name);
            return;
        }

        if ("message".equals(status)) {
            String text = jsonObj.get("text").getAsString();
            textInChat(name, text);
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
        messageSystem.sendMessage(msg);
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

        String message = gameMessageCreator.createMessageLeave(name);

        for (GameUser user : gameSession.getGameUsers()) {
            Message msg = new MsgFrnSendData(this.getAddress(),
                    messageSystem.getAddressService().getFrontendServiceAddress(),
                    user.getName(), message);
            this.sendMessage(msg);
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

            String message = gameMessageCreator.createMessageStartGame(gameSession, userName, gMResource.getGameTime());

            if (gameUser != null) {
                Message msg = new MsgFrnSendData(this.getAddress(),
                        messageSystem.getAddressService().getFrontendServiceAddress(),
                        userName, message);
                this.sendMessage(msg);
            } else {
                LOGGER.error("gameuser == null");
            }
        }
    }

    private void finishGame(@NotNull GameSession session) {
        String nameWinner = session.getNameWinner();
        String message = gameMessageCreator.createMessageGameOver(nameWinner);
        LOGGER.info("finish game");

        for (GameUser user : session.getGameUsers()) {
            Message msg = new MsgFrnSendData(this.getAddress(),
                    messageSystem.getAddressService().getFrontendServiceAddress(),
                    user.getName(), message);
            this.sendMessage(msg);

            //accountService.addScore(user.getName(), user.getScore());
            nameToGame.remove(user.getName());
        }
    }

    public void incrementScore(@NotNull String userName) {
        GameSession gameSession = nameToGame.get(userName);
        if (gameSession == null) {
            LOGGER.warn("userGameSession == null");
            return;
        }

        GameUser gameUser = gameSession.getGameUser(userName);
        if (gameUser == null) {
            LOGGER.warn("gameUser == null");
            return;
        }

        gameUser.incrementScore();

        String message = gameMessageCreator.createMessageIncrementScore(gameSession);
        for (GameUser user : gameSession.getGameUsers()) {
            Message msg = new MsgFrnSendData(this.getAddress(),
                    messageSystem.getAddressService().getFrontendServiceAddress(),
                    user.getName(), message);
            this.sendMessage(msg);
        }
    }

    public void textInChat(@NotNull String authorName, @NotNull String text) {
        GameSession gameSession = nameToGame.get(authorName);
        if (gameSession == null) {
            LOGGER.warn("userGameSession == null");
            return;
        }

        String message = gameMessageCreator.createMessageTextInChat(authorName, text);
        for (GameUser user : gameSession.getGameUsers()) {
            Message msg = new MsgFrnSendData(this.getAddress(),
                    messageSystem.getAddressService().getFrontendServiceAddress(),
                    user.getName(), message);
            this.sendMessage(msg);
        }
    }
}
