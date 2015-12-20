package com.wordpress.ilyaps.services.gamemechService.message;

import com.wordpress.ilyaps.services.gamemechService.GamemechService;
import com.wordpress.ilyaps.messageSystem.Address;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgGmmReceiveData extends MsgToGamemechService {
    @NotNull
    private final String name;
    @NotNull
    private final String data;

    public MsgGmmReceiveData(@NotNull Address from, @NotNull Address to, @NotNull String name, @NotNull String data) {
        super(from, to);
        this.data = data;
        this.name = name;
    }

    @Override
    protected void exec(@NotNull GamemechService service) {
        service.receiveData(name, data);
    }
}
