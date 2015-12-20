package com.wordpress.ilyaps.services.gamemechService.message;

import com.wordpress.ilyaps.services.gamemechService.GamemechService;
import com.wordpress.ilyaps.messageSystem.Address;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgGmmOpenSocket extends MsgToGamemechService {
    @NotNull
    private final String name;

    public MsgGmmOpenSocket(@NotNull Address from,@NotNull  Address to, @NotNull String name) {
        super(from, to);
        this.name = name;
    }

    @Override
    protected void exec(@NotNull GamemechService service) {
        service.addUser(name);
    }
}
