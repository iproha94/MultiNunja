package com.wordpress.ilyaps.services.servletsService.message;

import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.services.servletsService.ServletsService;

/**
 * Created by ilya on 18.12.15.
 */
public class MsgSrvRecieveScores extends MsgToServletsService {
    private String sessionId;
    private String scores;

    public MsgSrvRecieveScores(Address from, Address to, String sessionId, String scores) {
        super(from, to);
        this.sessionId = sessionId;
        this.scores = scores;
    }

    @Override
    protected void exec(ServletsService service) {
        service.gettingScore(sessionId, scores);
    }
}
