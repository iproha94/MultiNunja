package com.wordpress.ilyaps.accountService.message;

import com.wordpress.ilyaps.accountService.AccountService;
import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.message.MsgFrnAuthorized;
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
        final Message msg = new MsgFrnAuthorized(getTo(), getFrom(), sessionId, profile);
        service.sendMessage(msg);
    }
}
