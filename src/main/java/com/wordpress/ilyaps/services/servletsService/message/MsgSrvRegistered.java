package com.wordpress.ilyaps.services.servletsService.message;

import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Address;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ilya on 12.12.15.
 */
public class MsgSrvRegistered extends MsgToServletsService {
    @NotNull
    private final String email;
    private final UserProfile result;

    public MsgSrvRegistered(@NotNull Address from, @NotNull Address to, @NotNull String email, @Nullable UserProfile result) {
        super(from, to);
        this.email = email;
        this.result = result;
    }

    @Override
    protected void exec(@NotNull ServletsService service) {
        service.registered(email, result);
    }
}
