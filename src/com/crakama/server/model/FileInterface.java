package com.crakama.server.model;

import java.io.Serializable;

public interface FileInterface extends Serializable{
    String getUserName();
    String getAccessMode();
    int getSize();
    String getOwner();
}
