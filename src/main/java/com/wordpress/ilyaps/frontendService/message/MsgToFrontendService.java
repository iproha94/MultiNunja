package com.wordpress.ilyaps.frontendService.message;


import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.messageSystem.Abonent;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;

/**
 * @author e.shubin
 */
public abstract class MsgToFrontendService extends Message {
    public MsgToFrontendService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if (abonent instanceof FrontendService) {
            exec((FrontendService) abonent);
        }
    }

    protected abstract void exec(FrontendService service);
}
