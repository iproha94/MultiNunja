package com.wordpress.ilyaps.services.gamemechService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 28.11.15.
 */
public class GameMessageCreator {

    @NotNull
    public static String createMessageLeave(@NotNull String whoLeave) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "leave");
        result.addProperty("name", whoLeave);

        return result.toString();
    }

    @NotNull
    public static String createMessageStartGame(@NotNull GameSession gameSession,
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
    public static String createMessageGameOver(@NotNull GameSession gameSession) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "finish");

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

}
