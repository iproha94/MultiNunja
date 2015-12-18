package com.wordpress.ilyaps.services.servletsService.message;


import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.messageSystem.Abonent;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;

/**
 * @author e.shubin
 */
public abstract class MsgToServletsService extends Message {
    public MsgToServletsService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if (abonent instanceof ServletsService) {
            exec((ServletsService) abonent);
        }
    }

    protected abstract void exec(ServletsService service);
}
