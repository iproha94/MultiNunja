package com.wordpress.ilyaps.accountService;

import com.wordpress.ilyaps.messageSystem.Abonent;

/**
 * Created by ilya on 12.12.15.
 */
public interface AccountService  extends Abonent, Runnable {
    AccountServiceDAO getAccountServiceDAO();
}
