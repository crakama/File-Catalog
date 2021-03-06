package com.crakama.server.controller;

import com.crakama.common.tcp.MsgProtocol;
import com.crakama.common.tcp.MsgType;
import com.crakama.server.tcpnet.TCPFileHandler;

import java.io.*;
import java.net.Socket;

/**
 * This class is a new thread created per client to handle file transaction
 * using TCP Socket (java IO)communication and not part of RMI communication paradigm
 * This class is not needed in RMI communication because RMI is network-transparent
 * i.e handles communication under the hood. FileCatalog are in server root directory and thus can be downloaded by client
 */
//TODO:This class is supposed to act as a controller only and not communication thread
// TODO: Refactor to redirect network operations to NET package which handles transport layer communication
public class SFileTransfer implements Runnable{
    private Socket socket;
    private BufferedOutputStream bufOut;;
    private TCPFileHandler tcpFileHandler;
    public SFileTransfer(TCPFileHandler tcpFileHandler, Socket clientSocket) {
        this.socket = clientSocket;
        this.tcpFileHandler = tcpFileHandler;
    }
    /**
     * @code 0 indicates file not found, 1 indicates file found and download process begins
     * bufOut write to stream
     * bufIn reads file content from file object already fetched from stream
     *  byte[] to read a file in chunks of 1 kilobytes-1024, or 8 kilobytes-8192
     *  bufIn.read(buffer) reads from stream and stored into buffer array
     */
    @Override
    public void run() {
        while (socket.isConnected()){
            try{
               MsgProtocol msg = tcpFileHandler.message();
                String filename = msg.getMsgBody();
                switch (msg.getMsgType()){
                    case DOWNLOAD:

                        File fileObj = new File(filename);
                        if(!fileObj.exists()){
                            tcpFileHandler.sendResponse(MsgType.DOWNLOAD_NO,"File Not Found on Server");
                            tcpFileHandler.closeConnection();
                        }else{
                            tcpFileHandler.sendResponse(MsgType.DOWNLOAD_OK,filename);
                            tcpFileHandler.fromDIR_toBuffer(fileObj,socket);
                            tcpFileHandler.closeConnection();
                        }

                        break;
                    case UPLOAD:
                        tcpFileHandler.upload(filename,socket);
                        //TODO:This response stalls,can't send twice-file & response, why???
                        //tcpFileHandler.sendResponse(MsgType.UPLOAD_OK,filename);
                        break;
                }
            }catch (ClassNotFoundException|IOException e){

            }finally {
            }
        }

    }


}
