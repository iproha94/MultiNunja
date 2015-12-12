package com.wordpress.ilyaps.accountService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author e.shubin
 */
public interface AccountServiceDAO {
    boolean register(@NotNull String name, @NotNull String email, @NotNull String password);

    boolean authorization(@NotNull String email, @NotNull String password);

    UserProfile getUser(@Nullable String userEmail);
}
