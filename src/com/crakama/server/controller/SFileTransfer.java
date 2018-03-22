package com.crakama.server.controller;

import com.crakama.server.model.FileDao;
import com.crakama.server.model.FileInterface;
import com.crakama.server.model.FileCatalog;

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
    private BufferedReader bufReader;
    private BufferedInputStream bufIn;
    private BufferedOutputStream bufOut;
    private FileDao fileDao;
    public SFileTransfer(Socket clientSocket, String dbms, String datasource) {
        this.socket = clientSocket;
        fileDao = new FileDao(dbms,datasource);
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
            bufIn = new BufferedInputStream(socket.getInputStream());
            int codeRead = bufIn.read();
            //0 here means download operation requested and 1 means upload op
            if(codeRead == 2){
                String read = bufReader.readLine();
                File fileObj = new File(read);

                if(!fileObj.exists()){
                   byte code = (byte) 0;
                   bufOut.write(code);
                    closeConnection();
                }else{
                    fromDIR_toBuffer(fileObj);
                    closeConnection();
                }
            }else if(codeRead== 3){
                String filename = bufReader.readLine();
                upload(filename);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void fromDIR_toBuffer(File fileObj) throws IOException {
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
    }
    //TODO: Handle invalid path exception thrown when another process/windows is accessing directory
    private void upload(String filename){
        System.out.println("File UPloaded");
/*        String owner = "kate";
        FileInterface fileInterface = new FileCatalog(filename,owner);
        int filestatus = fileDao.saveToDB(fileInterface);
        if(filestatus == 0){
            System.out.println("Duplicate file name, File NOT saved into DB");
        }else{
            System.out.println("File saved");
        }*/

 /*    String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalog\\uploads\\";
        try {
            bufOut = new BufferedOutputStream(
                    new FileOutputStream( fileLocation+ filename));
            byte[] buffer = new byte[8192];
            int byteRead = 0;
            while ((byteRead = bufIn.read(buffer))!= -1){
                bufOut.write(buffer,0,byteRead);
                bufOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
