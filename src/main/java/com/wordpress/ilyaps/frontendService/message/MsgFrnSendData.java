package com.wordpress.ilyaps.frontendService.message;

import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgFrnSendData extends MsgToFrontendService {
    private String name;
    private String data;

    public MsgFrnSendData(Address from, Address to, String name, String data) {
        super(from, to);
        this.data = data;
        this.name = name;
    }

    @Override
    protected void exec(FrontendService service) {
        service.sendData(name, data);
    }
}
