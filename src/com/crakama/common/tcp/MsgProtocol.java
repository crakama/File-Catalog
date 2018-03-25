package com.crakama.common.tcp;

import java.io.Serializable;

public class MsgProtocol implements Serializable {
    private final MsgType msgType;
    private final String msgBody;
    // private final char charGuess;


    public MsgProtocol(MsgType msgType, String msgBody){
        this.msgType = msgType;
        this.msgBody = msgBody;
    }

    //    public MsgProtocol(char charGuess){
//        this.charGuess = charGuess;
//    }
    public String getMsgBody() {
        return msgBody;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType, String msgBody){

    }
}