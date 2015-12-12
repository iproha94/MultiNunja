package com.wordpress.ilyaps.messageSystem;


import com.wordpress.ilyaps.accountService.AccountService;
import com.wordpress.ilyaps.accountService.AccountServiceImpl1;
import com.wordpress.ilyaps.frontendService.FrontendService;

public final class AddressService {
    private Address frontendServiceAddress;
    private Address gamemechServiceAddress;
    private Address accountServiceAddress;


    public void registerFrontendService(FrontendService frontendService) {
        this.frontendServiceAddress = frontendService.getAddress();
    }

    public Address getFrontendServiceAddress() {
        return frontendServiceAddress;
    }

    public void registerAccountService(AccountService accountService) {
        this.accountServiceAddress = accountService.getAddress();
    }

    public Address getAccountServiceAddress() {
        return accountServiceAddress;
    }
}
