package com.crakama.client.net;


import com.crakama.common.ClientInterface;

import java.io.*;
import java.net.Socket;

/**
 * Network layer thread: Handles file operations using TCP Socket communication paradigm
 */
//TODO:Optimize code to handle operation in thread pool as opposed to creation one thread per client

public class ClientFileHandler implements Runnable{
    private ClientInterface clientCallbackInterf;
    private InputStream readData;
    private PrintWriter writeData;
    private String filename;
    Socket clientSocket;
    BufferedInputStream bufIn;

    public void start(String host, int port, ClientInterface clientCallbackInterf, String filename){
        try {
            this.clientSocket = new Socket(host,port);
            this.readData = clientSocket.getInputStream();
            this.filename = filename;
            this.clientCallbackInterf = clientCallbackInterf;
            this.writeData = new PrintWriter(clientSocket.getOutputStream(),true);
            this.bufIn = new BufferedInputStream(readData);

            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *@out Completes the download from client end(server already wrote from its buffer to stream)
     * and save to some location
     */
    @Override
    public void run() {
      //  while ((true)){
            try {
                writeData.println(filename);
                int code = bufIn.read();
                if(code == 1){
                    String location = download();
                    clientCallbackInterf.serverResponse("DOWNLOAD: Download Successful!!!, find it in "+location);
                }else {
                    clientCallbackInterf.serverResponse("File Not Found on the Server!!!, " +
                                                        "Check the name and try again");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
       // }
    }

    /**
     * Read data (bufIn) from a socket connection and write (bufOut) it to file in local computer
     */
    private String download(){
        String fileLocation = "D:\\Projects\\IdeaProjects" +
                "\\FileCatalog\\downloads\\";
        try {

            BufferedOutputStream bufOut = new BufferedOutputStream(
                    new FileOutputStream( fileLocation+ filename));
            byte[] buffer = new byte[8192];
            int byteRead = 0;
            while ((byteRead = bufIn.read(buffer))!= -1){
                bufOut.write(buffer,0,byteRead);
                bufOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLocation;
    }
}
