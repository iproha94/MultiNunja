package com.wordpress.ilyaps.messageSystem;


import com.wordpress.ilyaps.accountService.AccountService;
import com.wordpress.ilyaps.frontendService.FrontendService;
import com.wordpress.ilyaps.gamemechService.GamemechService;

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

    public void registerGamemechService(GamemechService gamemechService) {
        this.gamemechServiceAddress = gamemechService.getAddress();
    }

    public Address getGamemechServiceAddress() {
        return gamemechServiceAddress;
    }
}
