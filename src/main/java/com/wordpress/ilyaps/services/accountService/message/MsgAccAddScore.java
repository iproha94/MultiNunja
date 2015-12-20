package com.wordpress.ilyaps.services.accountService.message;

import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.services.accountService.AccountService;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 19.12.15.
 */
public class MsgAccAddScore extends MsgToAccountService {
    @NotNull
    private final String name;

    private final int score;

    public MsgAccAddScore(
            @NotNull Address from,
            @NotNull Address to,
            @NotNull String name,
            int score
    ) {
        super(from, to);
        this.score = score;
        this.name = name;
    }

    @Override
    protected void exec(@NotNull AccountService service) {
        service.addScore(name, score);
    }
}