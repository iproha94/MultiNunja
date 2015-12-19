package com.wordpress.ilyaps.services.accountService.dataset;

import org.jetbrains.annotations.NotNull;

public class Score {
    @NotNull
    private String name;
    private int score;

    public Score(@NotNull String name, int score) {
        this.name = name;
        this.score = score;
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
