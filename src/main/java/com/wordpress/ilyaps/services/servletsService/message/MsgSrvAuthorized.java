package com.wordpress.ilyaps.services.servletsService.message;

import com.wordpress.ilyaps.services.accountService.UserProfile;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgSrvAuthorized extends MsgToServletsService {
    private String sessionId;
    private String email;
    private UserProfile profile;

    public MsgSrvAuthorized(Address from, Address to, String email, String sessionId, UserProfile profile) {
        super(from, to);
        this.sessionId = sessionId;
        this.profile = profile;
        this.email = email;
    }

    @Override
    protected void exec(ServletsService service) {
        service.authorized(email, sessionId, profile);
    }
}
