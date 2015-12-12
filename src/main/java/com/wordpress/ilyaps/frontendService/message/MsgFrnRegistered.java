package com.wordpress.ilyaps.frontendService.message;

import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 12.12.15.
 */
public class MsgFrnRegistered extends MsgToFrontendService {
    private String email;
    private boolean result;

    public MsgFrnRegistered(Address from, Address to, String email, boolean result) {
        super(from, to);
        this.email = email;
        this.result = result;
    }

    @Override
    protected void exec(FrontendService service) {
        service.registered(email, result);
    }
}
