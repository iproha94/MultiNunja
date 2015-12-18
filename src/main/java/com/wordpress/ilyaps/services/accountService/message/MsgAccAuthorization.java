package com.wordpress.ilyaps.services.accountService.message;

import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.services.servletsService.message.MsgSrvAuthorized;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgAccAuthorization extends MsgToAccountService {
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String sessionId;

    public MsgAccAuthorization(
            @NotNull Address from,
            @NotNull Address to,
            @NotNull String sessionId,
            @NotNull String email,
            @NotNull String password
    ) {
        super(from, to);
        this.email = email;
        this.password = password;
        this.sessionId = sessionId;
    }

    @Override
    protected void exec(AccountService service) {
        final UserProfile profile = service.getAccountServiceDAO().authorization(sessionId, email, password);
        final Message msg = new MsgSrvAuthorized(getTo(), getFrom(), email, sessionId, profile);
        service.sendMessage(msg);
    }
}
