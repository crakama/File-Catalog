package com.crakama.server.model;

import java.io.Serializable;

public interface FileInterface extends Serializable{
    String getFileName();
    String getOwner();
    String getAccessMode();
    long getSize();
}
