package com.wordpress.ilyaps.services.gamemechService.message;

import com.wordpress.ilyaps.services.gamemechService.GamemechService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgGmmReceiveData extends MsgToGamemechService {
    private String name;
    private String data;

    public MsgGmmReceiveData(Address from, Address to, String name, String data) {
        super(from, to);
        this.data = data;
        this.name = name;
    }

    @Override
    protected void exec(GamemechService service) {
        service.receiveData(name, data);
    }
}
