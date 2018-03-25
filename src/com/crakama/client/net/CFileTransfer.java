package com.crakama.client.net;


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
    private ClientInterface clientCallbackInterf;
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
            this.clientCallbackInterf = clientCallbackInterf;
            this.fromServer = new ObjectInputStream(clientSocket.getInputStream());
            this.toServer = new ObjectOutputStream(clientSocket.getOutputStream());

            this.bufIn = new BufferedInputStream(readData);
            this.bufOut = new BufferedOutputStream(clientSocket.getOutputStream());

            new Thread(new ListenerThread(clientCallbackInterf)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(MsgType type, String body) throws IOException {
        MsgProtocol msg = new MsgProtocol(type,body);
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
        System.out.println("SENT TO SERVER");
    }

    /**
     * Read data (bufIn) from a socket connection and write (bufOut) it to file in local computer
     * @param filename
     */
    private String download(String filename) {
        System.out.println("download"+filename);
        String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalogAlpha\\downloads\\";
        try {

            bufOut = new BufferedOutputStream(
                    new FileOutputStream(fileLocation + filename));
            byte[] buffer = new byte[4096];
            int byteRead = 0;
            while ((byteRead = bufIn.read(buffer)) != -1) {
                bufOut.write(buffer, 0, byteRead);
                bufOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLocation;
    }

    private class ListenerThread implements Runnable {
        private final ClientInterface clientCallback;
        public ListenerThread(ClientInterface clientCallbackInterf) {
            this.clientCallback= clientCallbackInterf;
        }

        /**
         *@out Completes the download from client end(server already wrote from its buffer to stream)
         * and save to some location
         */
        @Override
        public void run() {
            while (clientSocket.isConnected()){
                try {
                    System.out.println("run");
                    MsgProtocol msg = (MsgProtocol) fromServer.readObject();
                    System.out.println("MsgProtocol"+msg);
                    switch (msg.getMsgType()) {
                        case DOWNLOAD_OK:
                            System.out.println("DOWNLOAD_OK");
                            String location = download(msg.getMsgBody());
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
}