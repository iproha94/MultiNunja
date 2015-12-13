package com.wordpress.ilyaps.frontendSockets;

import org.jetbrains.annotations.NotNull;

/**
 * Created by ilya on 28.11.15.
 */
public interface WebSocketService {
    void add(@NotNull GameWebSocket userSocket);

    boolean remove(@NotNull String name);

    void notify(@NotNull String userName, @NotNull String message);
}
