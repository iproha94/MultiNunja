package com.wordpress.ilyaps.services.servletsService;

/**
 * Created by ilya on 18.12.15.
 */
public enum UserState {
    PENDING_LEAVING,
    LEFT,
    UNSUCCESSFUL_LEFT,
    PENDING_AUTHORIZATION,
    SUCCESSFUL_AUTHORIZED,
    UNSUCCESSFUL_AUTHORIZED,
    PENDING_REGISTRATION,
    SUCCESSFUL_REGISTERED,
    UNSUCCESSFUL_REGISTERED
}
