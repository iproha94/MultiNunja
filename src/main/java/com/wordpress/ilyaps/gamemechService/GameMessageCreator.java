package com.wordpress.ilyaps.gamemechService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 28.11.15.
 */
public class GameMessageCreator {
    @NotNull
    private final GamemechService gamemechService;

    public GameMessageCreator(@NotNull GamemechService gamemechService) {
        this.gamemechService = gamemechService;
    }

    @NotNull
    public String createMessageLeave(@NotNull String whoLeave) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "leave");
        result.addProperty("name", whoLeave);

        return result.toString();
    }

    @NotNull
    public String createMessageStartGame(@NotNull GameSession gameSession,
                                         @NotNull String myName,
                                         int gameTime) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "start");
        result.addProperty("your_name", myName);
        result.addProperty("time_of_game", gameTime / 1000);

        JsonArray arr = new JsonArray();
        for (GameUser player : gameSession.getGameUsers()) {
            JsonObject guser = new JsonObject();
            guser.addProperty("name", player.getName());
            arr.add(guser);
        }

        result.add("players", arr);

        return result.toString();
    }

    @NotNull
    public String createMessageGameOver(@NotNull String nameWinner) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "finish");
        result.addProperty("win", nameWinner);

        return result.toString();
    }

    @NotNull
    public String createMessageIncrementScore(@NotNull GameSession gameSession) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "scores");

        JsonArray arr = new JsonArray();
        for (GameUser player : gameSession.getGameUsers()) {
            JsonObject guser = new JsonObject();
            guser.addProperty("name", player.getName());
            guser.addProperty("score", player.getScore());
            arr.add(guser);
        }
        result.add("players", arr);

        return result.toString();
    }

//    @NotNull
//    public static String createMessageListScores(@NotNull AccountService accountService, int amount) {
//        JsonObject result = new JsonObject();
//        result.addProperty("status", 200);
//
//        JsonArray arr = new JsonArray();
//        for (Score score : accountService.getListScore(amount)) {
//            JsonObject guser = new JsonObject();
//            guser.addProperty("name", score.getName());
//            guser.addProperty("score", score.getScore());
//            arr.add(guser);
//        }
//        result.add("players", arr);
//
//        //noinspection ConstantConditions
//        return result.toString();
//    }

    @NotNull
    public String createMessageTextInChat(@NotNull String authorName, @NotNull String text) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "message");
        result.addProperty("name", authorName);
        result.addProperty("text", text);

        return result.toString();
    }
}
