package com.wordpress.ilyaps.services.gamemechService;

import com.wordpress.ilyaps.resourceSystem.Resource;

/**
 * Created by ilya on 01.11.15.
 */
public class GMResource implements Resource {
    private int stepTime;
    private int gameTime;
    private int numberPlayers;

    public int getStepTime() {
        return stepTime;
    }

    public void setStepTime(int stepTime) {
        this.stepTime = stepTime;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }
}
