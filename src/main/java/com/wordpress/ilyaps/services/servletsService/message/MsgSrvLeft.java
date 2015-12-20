package com.wordpress.ilyaps.services.servletsService.message;

import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Address;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgSrvLeft extends MsgToServletsService {
    @NotNull
    private final String sessionId;
    @Nullable
    private final UserProfile profile;
    @NotNull
    private final String email;

    public MsgSrvLeft(@NotNull Address from, Address to, @NotNull String email, @NotNull String sessionId, @Nullable UserProfile profile) {
        super(from, to);
        this.sessionId = sessionId;
        this.profile = profile;
        this.email = email;
    }

    @Override
    protected void exec(@NotNull ServletsService service) {
        service.left(email, sessionId, profile);
    }
}
