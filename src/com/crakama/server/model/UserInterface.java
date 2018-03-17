package com.crakama.server.model;


import java.io.Serializable;

/**
 * Called by value(Serializable), only copy send to client, meaning its read-only
 */
public interface UserInterface extends Serializable {

    public String getUserName();
}
