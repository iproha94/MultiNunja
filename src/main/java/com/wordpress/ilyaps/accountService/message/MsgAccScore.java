package com.wordpress.ilyaps.accountService.message;

import com.wordpress.ilyaps.accountService.AccountService;
import com.wordpress.ilyaps.messageSystem.Address;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgAccScore extends MsgToAccountService {
    @NotNull
    private String name;

    private int score;

    public MsgAccScore(
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
