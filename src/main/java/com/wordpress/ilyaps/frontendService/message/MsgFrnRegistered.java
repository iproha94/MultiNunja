package com.wordpress.ilyaps.frontendService.message;

import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 12.12.15.
 */
public class MsgFrnRegistered extends MsgToFrontendService {
    private String email;
    private UserProfile result;

    public MsgFrnRegistered(Address from, Address to, String email, UserProfile result) {
        super(from, to);
        this.email = email;
        this.result = result;
    }

    @Override
    protected void exec(FrontendService service) {
        service.registered(email, result);
    }
}
