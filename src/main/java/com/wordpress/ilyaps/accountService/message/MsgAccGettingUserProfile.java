package com.wordpress.ilyaps.accountService.message;

import com.wordpress.ilyaps.accountService.AccountService;
import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.message.MsgFrnGetUserProfile;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgAccGettingUserProfile extends MsgToAccountService {
    @NotNull
    private String sessionId;

    public MsgAccGettingUserProfile(
            @NotNull Address from,
            @NotNull Address to,
            @NotNull String sessionId
    ) {
        super(from, to);
        this.sessionId = sessionId;
    }

    @Override
    protected void exec(AccountService service) {
        final UserProfile profile = service.getAccountServiceDAO().getAuthorizedUser(sessionId);
        final Message msg = new MsgFrnGetUserProfile(getTo(), getFrom(), sessionId, profile);
        service.sendMessage(msg);
    }
}
