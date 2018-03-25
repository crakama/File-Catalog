//package com.crakama.server.controller;
//
//import com.crakama.server.model.FileDao;
//import com.crakama.server.model.FileInterface;
//import com.crakama.server.model.FileCatalog;
//
//import java.io.*;
//import java.net.Socket;
//
///**
// * This class is a new thread created per client to handle file transaction
// * using TCP Socket (java IO)communication and not part of RMI communication paradigm
// * This class is not needed in RMI communication because RMI is network-transparent
// * i.e handles communication under the hood. FileCatalog are in server root directory and thus can be downloaded by client
// */
////TODO:This class is supposed to act as a controller only and not communication thread
//// TODO: Refactor to redirect network operations to NET package which handles transport layer communication
//public class SFileTransfer implements Runnable{
//    private Socket socket;
//    private BufferedReader bufReader;
//    private BufferedInputStream bufIn;
//    private BufferedOutputStream bufOut;
//    private FileDao fileDao;
//    public SFileTransfer(Socket clientSocket, String dbms, String datasource) {
//        this.socket = clientSocket;
//        fileDao = new FileDao(dbms,datasource);
//    }
//    /**
//     * @code 0 indicates file not found, 1 indicates file found and download process begins
//     * bufOut write to stream
//     * bufIn reads file content from file object already fetched from stream
//     *  byte[] to read a file in chunks of 1 kilobytes-1024, or 8 kilobytes-8192
//     *  bufIn.read(buffer) reads from stream and stored into buffer array
//     */
//    @Override
//    public void run() {
//        try {
//            bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            bufOut = new BufferedOutputStream(socket.getOutputStream());
//            bufIn = new BufferedInputStream(socket.getInputStream());
//            int codeRead = bufIn.read();
//            System.out.println("Value of coderead before IF:"+ codeRead);
//            //0 here means download operation requested and 1 means upload op
//            if(codeRead == 2){
//                String read = bufReader.readLine();
//                System.out.println("Value of read:"+ read);
//                File fileObj = new File(read);
//
//                if(!fileObj.exists()){
//                    byte code = (byte) 0;
//                    bufOut.write(code);
//                    closeConnection();
//                }else{
//                    fromDIR_toBuffer(fileObj);
//                    closeConnection();
//                }
//            }else if(codeRead== 3){
//                String filename = bufReader.readLine();
//                upload(filename);
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//    private void fromDIR_toBuffer(File fileObj) throws IOException {
//        byte code = (byte) 1;
//        bufOut.write(code);
//        //Start download -Read file from root/project directory and write to buffer, then to socket connection
//        bufIn = new BufferedInputStream(new FileInputStream(fileObj));
//        byte[] buffer = new byte[8192];
//        int byteRead = 0;
//        while ((byteRead = bufIn.read(buffer))!= -1){
//            bufOut.write(buffer,0,byteRead);
//            bufOut.flush();
//        }
//    }
//    //TODO: Handle invalid path exception thrown when another process/windows is accessing directory
//    private void upload(String filename){
//        String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalog\\uploads\\";
//        try {
//            bufOut = new BufferedOutputStream(
//                    new FileOutputStream( fileLocation.trim()+ filename));
//            byte[] buffer = new byte[8192];
//            int byteRead = 0;
//            while ((byteRead = bufIn.read(buffer))!= -1){
//                bufOut.write(buffer,0,byteRead);
//                bufOut.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private void closeConnection(){
//        try {
//            if(bufOut != null){
//                bufOut.close();
//            }
//            if(bufReader != null){
//                bufReader.close();
//            }
//            if(bufIn != null){
//                bufIn.close();
//            }
//            if(bufOut != null){
//                socket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

//
//
//package com.crakama.client.net;
//
//
//import com.crakama.client.view.ClientManager;
//import com.crakama.client.view.CmdType;
//import com.crakama.common.rmi.ClientInterface;
//import com.crakama.common.tcp.MsgProtocol;
//import com.crakama.common.tcp.MsgType;
//import com.crakama.server.tcpnet.TCPFileHandler;
//
//import java.io.*;
//import java.net.Socket;
//
///**
// * Network layer thread: Handles file operations using TCP Socket communication paradigm
// */
////TODO:Optimize code to handle operation in thread pool as opposed to creation one thread per client
//
//public class CFileTransfer {
//    //private ClientInterface clientCallbackInterf;
//    private InputStream readData;
//    private PrintWriter writeData;
//    Socket clientSocket;
//    BufferedInputStream bufIn;
//
//    private BufferedReader bufReader;
//    private BufferedOutputStream bufOut;
//    private ObjectInputStream fromServer;
//    private ObjectOutputStream toServer;
//    public CFileTransfer(){
//    }
//
//    public void start(String host, int port, ClientManager.ServerResponse serverResponse) {
//        try {
//            this.clientSocket = new Socket(host, port);
//            this.readData = clientSocket.getInputStream();
//            //this.clientCallbackInterf = clientCallbackInterf;
//            this.fromServer = new ObjectInputStream(clientSocket.getInputStream());
//            this.toServer = new ObjectOutputStream(clientSocket.getOutputStream());
//
//            this.bufIn = new BufferedInputStream(readData);
//            this.bufOut = new BufferedOutputStream(clientSocket.getOutputStream());
//
//
//            Thread clientThread = new Thread(new ListenerThread(serverResponse));
//            clientThread.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMsg(MsgType type, String body) throws IOException {
//        MsgProtocol msg = new MsgProtocol(type,body);
//        toServer.writeObject(msg);
//        toServer.flush();
//        toServer.reset();
//        System.out.println("SENT TO SERVER");
//    }
//
//    /**
//     * Read data (bufIn) from a socket connection and write (bufOut) it to file in local computer
//     * @param filename
//     */
//    private String download(String filename) {
//        System.out.println("download"+filename);
//        String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalogAlpha\\downloads\\";
//        try {
//
//            bufOut = new BufferedOutputStream(
//                    new FileOutputStream(fileLocation + filename));
//            byte[] buffer = new byte[4096];
//            int byteRead = 0;
//            while ((byteRead = bufIn.read(buffer)) != -1) {
//                bufOut.write(buffer, 0, byteRead);
//                bufOut.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return fileLocation;
//    }
//
//    private class ListenerThread implements Runnable {
//        //private final ClientInterface clientCallback;
//        private final ClientManager.ServerResponse clientCallback;
//        public ListenerThread(ClientManager.ServerResponse serverResponse) {
//            this.clientCallback= serverResponse;
//        }
//
//        /**
//         *@out Completes the download from client end(server already wrote from its buffer to stream)
//         * and save to some location
//         */
//        @Override
//        public void run() {
//            while (clientSocket.isConnected()){
//                try {
//                    System.out.println("run");
//                    MsgProtocol msg = (MsgProtocol) fromServer.readObject();
//                    System.out.println("MsgProtocol"+msg);
//                    switch (msg.getMsgType()) {
//                        case DOWNLOAD_OK:
//                            System.out.println("DOWNLOAD_OK");
//                            String location = download(msg.getMsgBody());
//                            clientCallback.handleServerResponse( "DOWNLOAD: Download Successful!!!, find it in "+ location);
//                            break;
//                        case UPLOAD:
//
//                            break;
//                    }
//                } catch (ClassNotFoundException | IOException e) {
//
//                } finally {
//                }
//            }
//        }
//    }
//}



//package com.crakama.client.view;
//
//import com.crakama.client.net.CFileTransfer;
//import com.crakama.common.rmi.ClientInterface;
//import com.crakama.common.rmi.ServerInterface;
//import com.crakama.common.tcp.MsgType;
//import com.crakama.common.tcp.TCPCLientInterface;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.rmi.RemoteException;
//import java.rmi.server.UnicastRemoteObject;
//
//public class ClientManager implements Runnable{
//    ServerInterface serverInterface;
//    //Thread fileworker;
//    private final String host = "localhost";
//    private final int port = 1213;
//    private boolean commandsReceived = false;
//    private boolean loginsession = false;
//    private BufferedReader userInput;
//    private final ClientInterface clientCallbackInterf;
//    CFileTransfer cFileTransfer;
//    public ClientManager() throws RemoteException {
//        clientCallbackInterf = new ClientStub();
//        cFileTransfer = new CFileTransfer();
//    }
//
//    public void start(ServerInterface serverInterface) {
//        this.serverInterface = serverInterface;
//        commandsReceived = true;
//        new Thread(this).start();
//    }
//
//    /**
//     * Inteprate CMD commands read and invoke necessary methods
//     */
//    public void run() {
//
//        while(commandsReceived){
//            try {
//                CmdReader cmdReader = new CmdReader(inputHandler());
//                switch (cmdReader.getCmd()){
//                    case LOGIN:case CONNECT:
//                        serverInterface.login(clientCallbackInterf,
//                                cmdReader.getParameters(1),
//                                cmdReader.getParameters(2));
//                        cFileTransfer.start(host,port, new ServerResponse());
//                        System.out.println("Main CM returned");
//                        loginsession = true;
//                        break;
//                    case LOGOUT:
//                        loginsession = false;
//                        break;
//                    case REGISTER:
//                        serverInterface.register(clientCallbackInterf,
//                                cmdReader.getParameters(1),
//                                cmdReader.getParameters(2));
//                        break;
//                    case UNREGISTER:
//                        serverInterface.unregister(clientCallbackInterf,
//                                cmdReader.getParameters(1),
//                                cmdReader.getParameters(2));
//                        break;
//                    case DOWNLOAD:
//                        if(loginsession==true){
//                            cFileTransfer.sendMsg(MsgType.DOWNLOAD,cmdReader.getParameters(1));
//
//                        }else {
//                            clientCallbackInterf.serverResponse("You need to Register and " +
//                                    "Login to View the file");
//                        }
//                        break;
//                    case UPLOAD:
//                        //TODO: Take care of same column name for file and user tables
//                        if(loginsession==true){
//                            serverInterface.checkfile(clientCallbackInterf,
//                                    cmdReader.getParameters(1));
//                        }else {
//                            clientCallbackInterf.serverResponse("You need to Register and " +
//                                    "Login to Upload the file");
//                        }
//                        break;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//
//    public String inputHandler() throws IOException {
//        System.out.println("Enter one of the following Commands to proceed:\n" +
//                " Use <localhost>, <name and password> or <name> in options\n");
//        for(CmdType cmd: CmdType.values()){
//            if(cmd == CmdType.NO_COMMAND){
//                continue;
//            }
//            System.out.println(cmd.toString() + " <option>");
//        }
//        this.userInput = new BufferedReader(new InputStreamReader(System.in));
//        String input = userInput.readLine();
//        return input;
//    }
//
//    /**
//     * Passed to server through call by reference, client's callback object
//     * The ONLY class that server can access by calling ClientInterface
//     */
//    private class ClientStub extends UnicastRemoteObject implements ClientInterface{
//
//        public ClientStub() throws RemoteException {
//            //U.R.O Handles exporting operations
//
//        }
//
//        @Override
//        public void serverResponse(String response) throws RuntimeException {
//            System.out.println("StartServer Response :" + response);
//        }
//    }
//
//    public class ServerResponse implements TCPCLientInterface {
//        @Override
//        public void handleServerResponse(String receivedMessage) {
//            System.out.println("//***-------------------------------------------------------------------------***\n\n"+receivedMessage+
//                    "\n\n***-------------------------------------------------------------------------***\n");
//        }
//    }
//}
