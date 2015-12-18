package com.wordpress.ilyaps.services.accountService;

import org.jetbrains.annotations.NotNull;


public class UserProfile {
    @NotNull
    private String name;
    @NotNull
    private String password;
    @NotNull
    private String email;

    public UserProfile(@NotNull String name, @NotNull String email, @NotNull String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

}
