package com.wordpress.ilyaps.frontendService.message;

import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgFrnAuthorized extends MsgToFrontendService {
    private String sessionId;
    private String email;
    private UserProfile profile;

    public MsgFrnAuthorized(Address from, Address to, String email, String sessionId, UserProfile profile) {
        super(from, to);
        this.sessionId = sessionId;
        this.profile = profile;
        this.email = email;
    }

    @Override
    protected void exec(FrontendService service) {
        service.authorized(email, sessionId, profile);
    }
}
