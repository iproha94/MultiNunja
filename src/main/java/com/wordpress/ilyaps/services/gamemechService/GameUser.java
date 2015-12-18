package com.wordpress.ilyaps.services.gamemechService;

import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 27.10.15.
 */
public class GameUser {
    @NotNull
    private final String name;
    private int score;

    public GameUser(@NotNull String name) {
        this.name = name;
        this.score = 0;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

}
