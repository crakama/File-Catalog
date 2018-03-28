package com.crakama.server.model;

import java.io.Serializable;

public interface FileEvents extends Serializable{
    String getFileName();
    String getDir();
    String getAccessor();
    void setFilename(String event);
    void setFileDIR(String event);
    void setAccessor(String accessor);
}
