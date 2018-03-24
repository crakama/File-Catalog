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
    //private String filename;
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


//
//        if(cmdType== CmdType.DOWNLOAD){
//
//            try {
//                bufOut.write(byt2);
//                bufOut.flush();
//
//                this.writeData = new PrintWriter(clientSocket.getOutputStream(),true);
//                writeData.println(filename);
//                //writeData.println("\0");
//                writeData.flush();
//                System.out.println("Value of writeData:"+ filename);
//                int downloadcode = bufIn.read();
//                if(downloadcode == 1){
//                    System.out.println("downloadcode 1"+ downloadcode);
//                    String location = download();
//                    clientCallbackInterf.serverResponse("DOWNLOAD: Download Successful!!!, find it in "+location);
//                }else {
//                    System.out.println("downloadcode 0"+ downloadcode);
//                    clientCallbackInterf.serverResponse("FileCatalog Not Found on the Server!!!, " +
//                            "Check the name and try again");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else if(cmdType==CmdType.UPLOAD){
//            byte byt3 = (byte) 3;
//            try {
//                bufOut.write(byt3);
//                bufOut.flush();
//                writeData.println(filename);
//                File file = new File(filename);
//                if(!file.exists()){
//                 clientCallbackInterf.serverResponse("The file you are trying to upload does not exist");
//                    closeConnection();
//                }else{
//                    bufIn = new BufferedInputStream(new FileInputStream(file));
//                    byte[] buffer = new byte[8192];
//                    int byteRead = 0;
//                    while ((byteRead = bufIn.read(buffer))!= -1){
//                        bufOut.write(buffer,0,byteRead);
//                        bufOut.flush();
//                    }
//                    closeConnection();
//                    clientCallbackInterf.serverResponse("UPLOAD: Upload Successful!!!");
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    public void sendMsg(MsgType type, String body) throws IOException {
        MsgProtocol msg = new MsgProtocol(type,body);
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
    }

    /**
     * Read data (bufIn) from a socket connection and write (bufOut) it to file in local computer
     * @param filename
     */
    private String download(String filename) {
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

    private void closeConnection() {
        try {
            if (bufOut != null) {
                bufOut.close();
            }
            if (bufReader != null) {
                bufReader.close();
            }
            if (bufIn != null) {
                bufIn.close();
            }
            if (bufOut != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void connect() {
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
            while (clientSocket.isConnected()) {
                try {
                    MsgProtocol msg = (MsgProtocol) fromServer.readObject();
                    switch (msg.getMsgType()) {
                        case DOWNLOAD_OK:
                            String location = download(msg.getMsgBody());
                            clientCallback.serverResponse( + location);
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