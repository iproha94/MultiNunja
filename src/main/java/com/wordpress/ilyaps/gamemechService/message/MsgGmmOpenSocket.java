package com.wordpress.ilyaps.gamemechService.message;

import com.wordpress.ilyaps.gamemechService.GamemechService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgGmmOpenSocket extends MsgToGamemechService {
    private String name;

    public MsgGmmOpenSocket(Address from, Address to, String name) {
        super(from, to);
        this.name = name;
    }

    @Override
    protected void exec(GamemechService service) {
        service.addUser(name);
    }
}
