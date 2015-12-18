package com.wordpress.ilyaps.multiNunjaGamemech;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wordpress.ilyaps.gamemechService.GameSession;
import com.wordpress.ilyaps.gamemechService.GameUser;
import com.wordpress.ilyaps.gamemechService.GamemechServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ilya on 13.12.15.
 */
public class MultiNunjaGamemech extends GamemechServiceImpl {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(MultiNunjaGamemech.class);

    private Random rand = new Random();

    public MultiNunjaGamemech() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::generateFruit, 0, 2, TimeUnit.SECONDS);
    }

    public void generateFruit() {
        String message = MultiNunjaMessageCreator.newFruit(rand.nextInt(100));
        for (GameSession session : getAllSessions()) {
            for (GameUser user : session.getGameUsers()) {
                sendData(user.getName(), message);
            }
        }
    }


    @Override
    public void receiveData(String name, String data) {
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

    public void incrementScore(@NotNull String userName) {
        GameSession gameSession = getNameToGame().get(userName);
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

        String message = MultiNunjaMessageCreator.incrementScore(gameSession);
        for (GameUser user : gameSession.getGameUsers()) {
            sendData(user.getName(), message);
        }
    }

    public void textInChat(@NotNull String authorName, @NotNull String text) {
        GameSession gameSession = getNameToGame().get(authorName);
        if (gameSession == null) {
            LOGGER.warn("userGameSession == null");
            return;
        }

        String message = MultiNunjaMessageCreator.textInChat(authorName, text);
        for (GameUser user : gameSession.getGameUsers()) {
            sendData(user.getName(), message);
        }
    }

}
