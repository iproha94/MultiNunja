package com.wordpress.ilyaps.services.accountService;

import com.wordpress.ilyaps.messageSystem.Abonent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 12.12.15.
 */
public interface AccountService  extends Abonent, Runnable {
    @NotNull
    AccountServiceDAO getAccountServiceDAO();
}
