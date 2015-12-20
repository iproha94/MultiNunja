package com.wordpress.ilyaps.messageSystem;


import com.wordpress.ilyaps.services.accountService.AccountService;
import com.wordpress.ilyaps.services.servletsService.ServletsService;
import com.wordpress.ilyaps.services.gamemechService.GamemechService;
import com.wordpress.ilyaps.services.socketsService.SocketsService;
import org.jetbrains.annotations.NotNull;

public final class AddressService {
    private Address servletsServiceAddress;
    private Address socketsServiceAddress;
    private Address gamemechServiceAddress;
    private Address accountServiceAddress;

    public void registerServletsService(ServletsService servletsService) {
        this.servletsServiceAddress = servletsService.getAddress();
    }

    @NotNull
    public Address getServletsServiceAddress() {
        return servletsServiceAddress;
    }

    public void registerSocketsService(SocketsService socketsService) {
        this.socketsServiceAddress = socketsService.getAddress();
    }

    @NotNull
    public Address getSocketsServiceAddress() {
        return socketsServiceAddress;
    }

    public void registerAccountService(AccountService accountService) {
        this.accountServiceAddress = accountService.getAddress();
    }

    @NotNull
    public Address getAccountServiceAddress() {
        return accountServiceAddress;
    }

    public void registerGamemechService(GamemechService gamemechService) {
        this.gamemechServiceAddress = gamemechService.getAddress();
    }

    @NotNull
    public Address getGamemechServiceAddress() {
        return gamemechServiceAddress;
    }
}
