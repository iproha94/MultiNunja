package com.wordpress.ilyaps.services.socketsService.message;

import com.wordpress.ilyaps.messageSystem.Address;
import com.wordpress.ilyaps.services.socketsService.SocketsService;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 13.12.15.
 */
public class MsgSckSendData extends MsgToSocketsService {
    @NotNull
    private final String name;
    @NotNull
    private final String data;

    public MsgSckSendData(@NotNull Address from, @NotNull Address to,
                          @NotNull String name, @NotNull String data) {
        super(from, to);
        this.data = data;
        this.name = name;
    }

    @Override
    protected void exec(@NotNull SocketsService service) {
        service.sendData(name, data);
    }
}
