package com.wordpress.ilyaps.services.accountService.message;

import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.services.servletsService.message.MsgSrvRegistered;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 12.12.15.
 */
public class MsgAccRegister extends MsgToAccountService {

    @NotNull
    private final String name;
    @NotNull
    private final String email;
    @NotNull
    private final String password;

    public MsgAccRegister(@NotNull Address from,
                          @NotNull Address to,
                          @NotNull String name,
                          @NotNull String email,
                          @NotNull String password)
    {
        super(from, to);
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    protected void exec(@NotNull AccountService service) {
        UserProfile result = service.register(name, email, password);
        final Message backMsg = new MsgSrvRegistered(
                this.getTo(),
                this.getFrom(),
                email,
                result
        );
        service.sendMessage(backMsg);
    }
}
