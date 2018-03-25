package com.crakama.client.net;


import com.crakama.client.view.ClientManager;
import com.crakama.client.view.CmdType;
import com.crakama.common.rmi.ClientInterface;
import com.crakama.common.tcp.MsgProtocol;
import com.crakama.common.tcp.MsgType;
import com.crakama.server.tcpnet.TCPFileHandler;

import java.io.*;
import java.net.Socket;

/**
 * Network layer thread: Handles file operations using TCP Socket communication paradigm
 */
//TODO:Optimize code to handle operation in thread pool as opposed to creation one thread per client

public class CFileTransfer {
    private InputStream readData;
    private PrintWriter writeData;
    Socket clientSocket;
    BufferedInputStream bufIn;
    private BufferedReader bufReader;
    private BufferedOutputStream bufOut;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    public CFileTransfer(){
        }

    public void start(String host, int port, ClientInterface clientCallbackInterf) {
        try {
            this.clientSocket = new Socket(host, port);
            this.readData = clientSocket.getInputStream();
            this.fromServer = new ObjectInputStream(clientSocket.getInputStream());
            this.toServer = new ObjectOutputStream(clientSocket.getOutputStream());

            Thread clientThread = new Thread(new ListenerThread(clientCallbackInterf,readData));
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(MsgType type, String body) throws IOException {
        MsgProtocol msg = new MsgProtocol(type,body);
        toServer.writeObject(msg);
        toServer.flush();
    }

    private class ListenerThread implements Runnable {
        private final ClientInterface clientCallback;
        private InputStream stream;
        public ListenerThread(ClientInterface clientCallbackInterf, InputStream stream) {
            this.clientCallback= clientCallbackInterf;
            this.stream = stream;
        }

        /**
         *@out Completes the download from client end(server already wrote from its buffer to stream)
         * and save to some location
         */
        @Override
        public void run() {
            while (clientSocket.isConnected()){
                try {
                    MsgProtocol msg = (MsgProtocol) fromServer.readObject();
                    switch (msg.getMsgType()) {
                        case DOWNLOAD_OK:
                            String location = download(msg.getMsgBody(),stream);
                            clientCallback.serverResponse( "DOWNLOAD: Download Successful!!!, find it in "+ location);
                            break;
                        case UPLOAD:

                            break;
                    }
                } catch (ClassNotFoundException | IOException e) {

                } finally {
                }
            }
        }
    }

    /**
     * Read data (bufIn) from a socket connection and write (bufOut) it to file in local computer
     * @param filename
     * @param stream
     */
    private String download(String filename, InputStream stream) {
        String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalogAlpha\\downloads\\";
        try {
            try (BufferedInputStream bis = new BufferedInputStream(stream);
                 BufferedOutputStream bufOut = new BufferedOutputStream(
                         new FileOutputStream(fileLocation + filename))
            ) {

                byte[] buf = new byte[8192];
                while (bis.read(buf, 0, buf.length) != -1) {
                    bufOut.write(buf, 0, buf.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileLocation;
    }

    //Start download -Read file from root/project directory and write to buffer, then to socket connection
    public void from_C_DIR_toBuffer(File fileObj) throws IOException {
        try {
            try (BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(fileObj));
                 BufferedOutputStream bufOut = new BufferedOutputStream(clientSocket.getOutputStream())
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
}