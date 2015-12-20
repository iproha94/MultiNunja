package com.wordpress.ilyaps.services.servletsService;

import com.wordpress.ilyaps.services.accountService.dataset.UserProfile;
import com.wordpress.ilyaps.messageSystem.Abonent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author e.shubin
 */
public interface ServletsService extends Abonent, Runnable {
    void registered(@NotNull String name, @Nullable UserProfile result);

    void registerUser(@NotNull String name, @NotNull String email, @NotNull String password);

    void authorized(@NotNull String email, @NotNull String sessionId, @Nullable UserProfile result);

    void authorizationUser(@NotNull String email, @NotNull String password, @NotNull String sessionId);

    void left(@NotNull String email, @NotNull String sessionId, @Nullable UserProfile result);

    void leaveUser(@NotNull String email, @NotNull String sessionId);

    @Nullable UserState getUserState(@NotNull String email);

    @Nullable UserProfile getUserProfile(@NotNull String sessionId);

    void removeUserProfile(@NotNull String sessionId);

    void removeUserState(@NotNull String email);

    void clearAll();
}
