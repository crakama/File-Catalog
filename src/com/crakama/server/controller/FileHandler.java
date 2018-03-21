package com.crakama.server.controller;

import java.io.*;
import java.net.Socket;

/**
 * This class is a new thread created per client to handle file transaction
 * using TCP Socket (java IO)communication and not part of RMI communication paradigm
 * This class is not needed in RMI communication because RMI is network-transparent
 * i.e handles communication under the hood. Files are in server root directory and thus can be downloaded by client
 */
//TODO:This class is supposed to act as a controller only and not communication thread
// TODO: Refactor to redirect network operations to NET package which handles transport layer communication
public class FileHandler implements Runnable{
    private Socket socket;
    private BufferedReader bufReader;
    private BufferedInputStream bufIn;
    private BufferedOutputStream bufOut;
    public FileHandler(Socket clientSocket) {
        this.socket = clientSocket;
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
        try {
            bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufOut = new BufferedOutputStream(socket.getOutputStream());

            String read = bufReader.readLine();
            File fileObj = new File(read);

            if(!fileObj.exists()){
                //TODO:Set client's reaction upon receipt of byte 0, client need to be listening on a different thread
               byte code = (byte) 0;
               bufOut.write(code);
                closeConnection();
            }else{
                byte code = (byte) 1;
                bufOut.write(code);
                //Start download -Read file from root/project directory and write to buffer, then to socket connection
                bufIn = new BufferedInputStream(new FileInputStream(fileObj));
                byte[] buffer = new byte[8192];
                int byteRead = 0;
                while ((byteRead = bufIn.read(buffer))!= -1){
                    bufOut.write(buffer,0,byteRead);
                    bufOut.flush();
                }
                closeConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void closeConnection(){
        try {
        if(bufOut != null){
            bufOut.close();
            }
            if(bufReader != null){
                bufReader.close();
            }
            if(bufIn != null){
                bufIn.close();
            }
            if(bufOut != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
