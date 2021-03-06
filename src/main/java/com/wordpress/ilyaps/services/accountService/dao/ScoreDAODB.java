package com.wordpress.ilyaps.services.accountService.dao;

import com.wordpress.ilyaps.databaseHelpers.DBExecutor;
import com.wordpress.ilyaps.services.accountService.dataset.Score;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilya on 29.11.15.
 */
public class ScoreDAODB {
    @NotNull
    static final Logger LOGGER = LogManager.getLogger(ScoreDAODB.class);
    @NotNull
    private final Connection connection;

    public ScoreDAODB(@NotNull Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS score ( " +
                "  name VARCHAR(45) NOT NULL, " +
                "  score INT NULL DEFAULT 0, " +
                "  CONSTRAINT fk_score_1 " +
                "    FOREIGN KEY (name) " +
                "    REFERENCES user (name) " +
                "    ON DELETE NO ACTION " +
                "    ON UPDATE NO ACTION) " +
                "ENGINE = InnoDB;";

        DBExecutor.execUpdate(connection, query);
    }

    public int insert(@NotNull Score score) throws SQLException {
        String query = "insert into score (name, score) " +
                "values ( '" + score.getName() + "' ,  "
                + score.getScore() + " )";

        return DBExecutor.execUpdate(connection, query);
    }

    @NotNull
    public List<Score> read(int start, int amount) throws SQLException {
        List<Score> list = new ArrayList<>(amount);
        String query = "select * from score order by score desc limit " + start + " , " + amount + " ;";
                DBExecutor.execQuery(connection, query,
                        result -> {
                            while (result.next()) {
                                Score score = new Score(result.getString("name"), result.getInt("score"));
                                list.add(score);
                            }
                            return null;
                        });

        return list;
    }

    @NotNull
    public List<Score> read(@NotNull String name) throws SQLException {
        List<Score> list = new ArrayList<>();
        String query = "select * from score where name = '" + name + "' order by score desc;";
        DBExecutor.execQuery(connection, query,
                result -> {
                    while (result.next()) {
                        Score score = new Score(result.getString("name"), result.getInt("score"));
                        list.add(score);
                    }
                    return null;
                });

        return list;
    }

    public int count() throws SQLException {
        String query = "SELECT count(*) FROM score";

        return DBExecutor.execQuery(connection, query,
                result -> {
                    if (result.next()) {
                        return result.getInt(1);
                    } else {
                        LOGGER.warn("count score: result == null");
                        return 0;
                    }
                });
    }

    public int deleteAll() throws SQLException {
        return DBExecutor.execUpdate(connection, "delete from score ");
    }
}
