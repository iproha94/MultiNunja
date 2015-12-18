package com.wordpress.ilyaps.services.accountService.message;

import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.services.servletsService.message.MsgSrvRecieveScores;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgAccGetScore extends MsgToAccountService {
    @NotNull
    private String sessionId;
    private int start;
    private int amount;

    public MsgAccGetScore(
            @NotNull Address from,
            @NotNull Address to,
            @NotNull String sessionId,
            int start,
            int amount
    ) {
        super(from, to);
        this.sessionId = sessionId;
        this.start = start;
        this.amount = amount;
    }

    @Override
    protected void exec(AccountService service) {
        final String strscore = service.getAccountServiceDAO().getScore(start, amount);
        final Message msg = new MsgSrvRecieveScores(getTo(), getFrom(), sessionId, strscore);
        service.sendMessage(msg);
    }
}
