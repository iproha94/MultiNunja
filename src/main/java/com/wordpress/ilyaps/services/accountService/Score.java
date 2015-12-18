package com.wordpress.ilyaps.services.accountService;

import org.jetbrains.annotations.NotNull;

/**
 * Created by v.chibrikov on 13.09.2014.
 */
public class Score {
    @NotNull
    private String name;
    private int score;

    public Score(@NotNull String name, int score) {
        this.name = name;
        this.score = score;
    }

    public Score() {
        this.name = "";
        this.score = 0;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
