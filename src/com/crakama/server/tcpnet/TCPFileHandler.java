package com.crakama.server.tcpnet;

import com.crakama.common.tcp.MsgProtocol;
import com.crakama.common.tcp.MsgType;

import java.io.*;
import java.net.Socket;

public class TCPFileHandler {
        private ObjectOutputStream toClient;
        private ObjectInputStream fromClient;

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
    }

    //Start download -Read file from root/project directory and write to buffer, then to socket connection
    public void fromDIR_toBuffer(File fileObj, Socket socket) throws IOException {
        try {
            try (BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(fileObj));
                 BufferedOutputStream bufOut = new BufferedOutputStream(socket.getOutputStream())
                  ) {

                byte[] buf = new byte[8192];
                while (bis.read(buf, 0, buf.length) != -1) {
                    bufOut.write(buf, 0, buf.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void closeConnection(){
            //TODO: Close connections
    }

    }

