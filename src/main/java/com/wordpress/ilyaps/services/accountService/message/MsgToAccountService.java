package com.wordpress.ilyaps.services.accountService.message;

import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.messageSystem.Abonent;
import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.messageSystem.Message;
import org.jetbrains.annotations.NotNull;

/**
 * @author e.shubin
 */
public abstract class MsgToAccountService extends Message {
    public MsgToAccountService(@NotNull Address from, @NotNull Address to) {
        super(from, to);
    }

    @Override
    public final void exec(Abonent abonent) {
        if (abonent instanceof AccountService) {
            exec((AccountService) abonent);
        }
    }

    protected abstract void exec(@NotNull AccountService service);
}
