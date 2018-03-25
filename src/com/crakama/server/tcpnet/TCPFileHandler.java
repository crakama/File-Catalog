package com.crakama.server.tcpnet;

import com.crakama.common.tcp.MsgProtocol;
import com.crakama.common.tcp.MsgType;

import java.io.*;
import java.net.Socket;

public class TCPFileHandler {
        private ObjectOutputStream toClient;
        private ObjectInputStream fromClient;
        private BufferedInputStream bufIn;
        private BufferedOutputStream bufOut;

        public TCPFileHandler(Socket clientSocket) throws IOException {
            this.toClient = new ObjectOutputStream(clientSocket.getOutputStream());
            this.fromClient = new ObjectInputStream(clientSocket.getInputStream());
        }

        public MsgProtocol message() throws IOException, ClassNotFoundException {
            MsgProtocol msg = (MsgProtocol) fromClient.readObject();
            return msg;
        }

    public void sendResponse(MsgType msgType, String response) throws IOException {
        MsgProtocol msgProtocol = new MsgProtocol(msgType,response);
        toClient.writeObject(msgProtocol);
        toClient.flush();
        toClient.reset();
    }

    public void fromDIR_toBuffer(File fileObj, Socket socket) throws IOException {
        System.out.println("FROM CLIENT"+fileObj);
        //Start download -Read file from root/project directory and write to buffer, then to socket connection
        bufIn = new BufferedInputStream(new FileInputStream(fileObj));
        bufOut = new BufferedOutputStream(socket.getOutputStream());
        byte[] buffer = new byte[4096];
        int byteRead = 0;
        while ((byteRead = bufIn.read(buffer))!= -1){
            bufOut.write(buffer,0,byteRead);
            bufOut.flush();
        }
    }


    public void closeConnection(){
            //TODO: Close connections
    }

    }

