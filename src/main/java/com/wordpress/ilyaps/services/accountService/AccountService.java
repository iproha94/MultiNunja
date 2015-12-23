package com.wordpress.ilyaps.services.accountService;

import com.wordpress.ilyaps.messageSystem.Abonent;
import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ilya on 12.12.15.
 */
public interface AccountService  extends Abonent, Runnable {
    @Nullable
    UserProfile register(@NotNull String name, @NotNull String email, @NotNull String password);

    @Nullable
    UserProfile authorization(@NotNull String sessionId, @NotNull String email, @NotNull String password);

    @Nullable
    UserProfile fastauthorization(@NotNull String sessionId, @NotNull String username, @NotNull String password);

    @Nullable
    UserProfile leaving(@NotNull String sessionId);

    @Nullable
    UserProfile getRegisteredUser(@NotNull String email);

    @Nullable
    UserProfile getAuthorizedUser(@NotNull String sessionId);

    void addScore(@NotNull String name, int score);

    @NotNull
    String getScore(int start, int amount);

    @NotNull
    String getScore(@NotNull String name);

    int countRegisteredUser();

    int countAuthorizedUser();

    void removeAllSessions();
}
