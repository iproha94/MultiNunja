package com.wordpress.ilyaps.accountService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilya on 12.12.15.
 */
public class AccountServiceDAOImpl1 implements AccountServiceDAO {
    @NotNull
    private final Map<String, UserProfile> users = new HashMap<>();
    @NotNull
    private final Map<String, UserProfile> sessions = new HashMap<>();


    @Override
    public boolean register(@NotNull String name, @NotNull String email, @NotNull String password) {
        synchronized (users) {
            if (users.containsKey(email)) {
                return false;
            }
            users.put(email, new UserProfile(name, email, password));
            return true;
        }
    }

    @Override
    public boolean authorization(@NotNull String email, @NotNull String password) {
        return false;
    }

    @Override
    public UserProfile getUser(@Nullable String userEmail) {
        return null;
    }
}
