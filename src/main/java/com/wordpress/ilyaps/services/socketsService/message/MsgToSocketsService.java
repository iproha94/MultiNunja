package com.wordpress.ilyaps.services.socketsService.message;


import com.wordpress.ilyaps.messageSystem.Abonent;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import com.wordpress.ilyaps.services.socketsService.SocketsService;
import org.jetbrains.annotations.NotNull;

/**
 * @author e.shubin
 */
public abstract class MsgToSocketsService extends Message {
    public MsgToSocketsService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if (abonent instanceof SocketsService) {
            exec((SocketsService) abonent);
        }
    }

    protected abstract void exec(@NotNull SocketsService service);
}
