package com.crakama.common.tcp;

/**
 * For some reason, TCP operations stalls when RMI-based interface -ClientInterface is called.
 *Thus the need of a separate TCP-based interface.
 */
public interface TCPCLientInterface {
    void handleServerResponse(String msg);
}
