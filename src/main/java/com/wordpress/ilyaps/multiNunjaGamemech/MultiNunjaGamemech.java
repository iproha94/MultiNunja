package com.wordpress.ilyaps.multiNunjaGamemech;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wordpress.ilyaps.services.gamemechService.GameSession;
import com.wordpress.ilyaps.services.gamemechService.GameUser;
import com.wordpress.ilyaps.services.gamemechService.GamemechServiceImpl;
import com.wordpress.ilyaps.resourceSystem.ResourcesContext;
import com.wordpress.ilyaps.serverHelpers.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ilya on 13.12.15.
 */
public class MultiNunjaGamemech extends GamemechServiceImpl {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(MultiNunjaGamemech.class);

    private MultiNunjaResource multiNunjaResource;

    private int maxFruit;
    private int pointerFruit = 0;
    private List<Fruit> fruitList = new ArrayList<>(maxFruit);


    public MultiNunjaGamemech() {

        GameContext gameContext = GameContext.getInstance();
        ResourcesContext resourcesContext = (ResourcesContext) gameContext.get(ResourcesContext.class);
        this.multiNunjaResource = (MultiNunjaResource) resourcesContext.get(MultiNunjaResource.class);

        maxFruit = multiNunjaResource.getMaxFruit();
        int periodGenerate = multiNunjaResource.getPeriodGenerate();

        for (int i = 0; i < maxFruit; ++i) {
            Fruit fruit = new Fruit();
            fruit.generateFruit(i);
            fruitList.add(fruit);
        }

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::generateFruit, 0, periodGenerate, TimeUnit.MILLISECONDS);
    }

    public void generateFruit() {
        pointerFruit++;
        pointerFruit %= maxFruit;
        String message = MultiNunjaMessageCreator.newFruit(fruitList.get(pointerFruit));
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

        if ("myshot".equals(status)) {
            int fruitId = jsonObj.get("id").getAsInt();
            LOGGER.info("myshot фрукта id = " + fruitId + " от игрока " + name);
            myShot(name, fruitId);
            return;
        }

        if ("increment".equals(status)) {
            incrementScore(name);
            return;
        }

        if ("message".equals(status)) {
            String text = jsonObj.get("text").getAsString();
            textInChat(name, text);
            return;
        }
    }

    private void myShot(String name, int fruitId) {
        GameSession gameSession = getNameToGame().get(name);
        if (gameSession == null) {
            LOGGER.warn("userGameSession == null");
            return;
        }

        GameUser gameUser = gameSession.getGameUser(name);
        if (gameUser == null) {
            LOGGER.warn("gameUser == null");
            return;
        }

        gameUser.incrementScore();

        String message = MultiNunjaMessageCreator.enemyShot(name, fruitId, gameSession);
        for (GameUser user : gameSession.getGameUsers()) {
            sendData(user.getName(), message);
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
