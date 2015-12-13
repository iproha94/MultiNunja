package com.wordpress.ilyaps.gamemechService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class GamemechMultiNunja implements  SpecificGame {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(GamemechMultiNunja.class);

    @NotNull
    private final GamemechService gamemechService;

    public GamemechMultiNunja(GamemechService gamemechService) {
        this.gamemechService = gamemechService;
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
        GameSession gameSession = gamemechService.getNameToGame().get(userName);
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
            gamemechService.sendData(user.getName(), message);
        }
    }

    public void textInChat(@NotNull String authorName, @NotNull String text) {
        GameSession gameSession = gamemechService.getNameToGame().get(authorName);
        if (gameSession == null) {
            LOGGER.warn("userGameSession == null");
            return;
        }

        String message = MultiNunjaMessageCreator.textInChat(authorName, text);
        for (GameUser user : gameSession.getGameUsers()) {
            gamemechService.sendData(user.getName(), message);
        }
    }



}
