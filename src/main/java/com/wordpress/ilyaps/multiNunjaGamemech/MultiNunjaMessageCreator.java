package com.wordpress.ilyaps.multiNunjaGamemech;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wordpress.ilyaps.gamemechService.GameSession;
import com.wordpress.ilyaps.gamemechService.GameUser;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MultiNunjaMessageCreator {
    @NotNull
    public static String incrementScore(@NotNull GameSession gameSession) {
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
    @NotNull
    public static String textInChat(@NotNull String authorName, @NotNull String text) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "message");
        result.addProperty("name", authorName);
        result.addProperty("text", text);

        return result.toString();
    }

    public static String newFruit(Fruit fruit) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "newfruit");
        result.addProperty("id", fruit.getId());

        JsonArray arr = new JsonArray();
        arr.add(fruit.getA());
        arr.add(fruit.getB());
        arr.add(fruit.getC());
        result.add("koef", arr);

        return result.toString();
    }

    public static String enemyShot(String name, int fruitId) {
        JsonObject result = new JsonObject();
        result.addProperty("status", "enemyshot");
        result.addProperty("name", name);
        result.addProperty("id", fruitId);

        return result.toString();
    }
}
