package com.wordpress.ilyaps.services.servletsService.message;

import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 12.12.15.
 */
public class MsgSrvRegistered extends MsgToServletsService {
    private String email;
    private UserProfile result;

    public MsgSrvRegistered(Address from, Address to, String email, UserProfile result) {
        super(from, to);
        this.email = email;
        this.result = result;
    }

    @Override
    protected void exec(ServletsService service) {
        service.registered(email, result);
    }
}
