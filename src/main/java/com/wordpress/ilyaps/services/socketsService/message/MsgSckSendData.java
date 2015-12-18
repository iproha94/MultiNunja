package com.wordpress.ilyaps.services.socketsService.message;

import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.services.socketsService.SocketsService;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgSckSendData extends MsgToSocketsService {
    private String name;
    private String data;

    public MsgSckSendData(Address from, Address to, String name, String data) {
        super(from, to);
        this.data = data;
        this.name = name;
    }

    @Override
    protected void exec(SocketsService service) {
        service.sendData(name, data);
    }
}
