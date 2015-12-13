package com.wordpress.ilyaps.accountService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author e.shubin
 */
public interface AccountServiceDAO {
    @Nullable
    UserProfile register(@NotNull String name, @NotNull String email, @NotNull String password);

    @Nullable
    UserProfile authorization(@NotNull String sessionId, @NotNull String email, @NotNull String password);

    @Nullable
    UserProfile leaving(@NotNull String sessionId);

    @Nullable
    UserProfile getRegisteredUser(@NotNull String email);

    @Nullable
    UserProfile getAuthorizedUser(@NotNull String sessionId);

    void addScore(@NotNull String name, int score);
}
