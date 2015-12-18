package com.wordpress.ilyaps.services.servletsService.message;

import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgSrvLeft extends MsgToServletsService {
    private String sessionId;
    private UserProfile profile;
    private String email;

    public MsgSrvLeft(Address from, Address to, String email, String sessionId, UserProfile profile) {
        super(from, to);
        this.sessionId = sessionId;
        this.profile = profile;
        this.email = email;
    }

    @Override
    protected void exec(ServletsService service) {
        service.left(email, sessionId, profile);
    }
}
