package com.wordpress.ilyaps.frontendService.message;

import com.wordpress.ilyaps.accountService.UserProfile;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.messageSystem.Address;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgFrnAuthorized extends MsgToFrontendService {
    private String sessionId;
    private UserProfile profile;

    public MsgFrnAuthorized(Address from, Address to, String sessionId, UserProfile profile) {
        super(from, to);
        this.sessionId = sessionId;
        this.profile = profile;
    }

    @Override
    protected void exec(FrontendService service) {
        service.authorized(sessionId, profile);
    }
}
