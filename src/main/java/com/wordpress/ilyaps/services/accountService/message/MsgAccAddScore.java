package com.wordpress.ilyaps.services.accountService.message;

import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.services.accountService.AccountService;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 19.12.15.
 */
public class MsgAccAddScore extends MsgToAccountService {
    @NotNull
    private String name;

    private int score;

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
    protected void exec(AccountService service) {
        service.getAccountServiceDAO().addScore(name, score);
    }
}