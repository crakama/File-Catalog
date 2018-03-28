package com.crakama.client.view;

import com.crakama.client.net.CFileTransfer;
import com.crakama.common.rmi.ClientInterface;
import com.crakama.common.rmi.ServerInterface;
import com.crakama.common.tcp.MsgType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientManager implements Runnable{
    ServerInterface serverInterface;
    private final String host = "localhost";
    private final int port = 1213;
    private boolean commandsReceived = false;
    private boolean loginsession = false;
    private BufferedReader userInput;
    private ClientInterface clientCallbackInterf;
    private CFileTransfer cFileTransfer;
    boolean savedTODB = false;
    private ArrayList<String> filemonitor;
    Queue<String> loggedInUser = new ConcurrentLinkedQueue<>();
    public ClientManager() throws RemoteException {

    }

    public void start(ServerInterface serverInterface) {
        try {
        this.serverInterface = serverInterface;
        commandsReceived = true;
        cFileTransfer = new CFileTransfer();
        clientCallbackInterf = new ClientStub();
            filemonitor = new ArrayList<>();
            new Thread(this).start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Inteprate CMD commands read and invoke necessary methods
     */
    public void run() {

        while(commandsReceived){
            try {
                CmdReader cmdReader = new CmdReader(inputHandler());
                switch (cmdReader.getCmd()){
                    case LOGIN:case CONNECT:
                        serverInterface.login(clientCallbackInterf,
                                cmdReader.getParameters(1),
                                cmdReader.getParameters(2));
                        cFileTransfer.start(host,port, new ClientStub());
                        loginsession = true;
                    break;
                    case LOGOUT:
                        String user = loggedInUser.poll();
                        loggedInUser.clear();
                        serverInterface.removeMonitoredFiles();
                        loginsession = false;
                        clientCallbackInterf.serverResponse("User by name, "+user+
                                " has been successfully logged out of the system ");
                    break;
                    case REGISTER:
                        serverInterface.register(clientCallbackInterf,
                                cmdReader.getParameters(1),
                                cmdReader.getParameters(2));
                    break;
                    case UNREGISTER:
                        serverInterface.unregister(clientCallbackInterf,
                                cmdReader.getParameters(1),
                                cmdReader.getParameters(2));
                    break;
                    case DOWNLOAD:
                        if(loginsession==true){
                            cFileTransfer.sendMsg(MsgType.DOWNLOAD,cmdReader.getParameters(1));

                        }else { clientCallbackInterf.serverResponse("You need to Register and " +
                                    "Login to View the file"); }
                        break;
                    case UPLOAD:
                        if(loginsession==true){
                            uploadFile(clientCallbackInterf,
                                    cmdReader.getParameters(1),cmdReader.getParameters(2));
                        }else { clientCallbackInterf.serverResponse("You need to Register and " +
                                    "Login to Upload the file"); }
                        break;
                    case READ:
                        permission(cmdReader.getCmd(),clientCallbackInterf,loggedInUser.peek(),
                                cmdReader.getParameters(1),null);
                        break;
                    case EDIT:
                        permission(cmdReader.getCmd(),clientCallbackInterf,
                                loggedInUser.peek(), cmdReader.getParameters(1),
                                cmdReader.getParameters(2));
                        break;
                    case LIST:

                        serverInterface.listfiles(clientCallbackInterf,loggedInUser.peek());
                        break;
                    case NOTIFY:
                        serverInterface.fileMonitor(clientCallbackInterf,cmdReader.getParameters(1));
                        //TODO: Alternative solution, store all notification subscription on DB &
                        // TODO: use REGISTER/DE-REGISTER mechanism, to avoid loops
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadFile(ClientInterface clientCallbackInterf, String param1, String param2) throws IOException {
        long filesize = getfileSize(param1);
        serverInterface.checkfile(clientCallbackInterf,
                param1,loggedInUser.peek(),param2,filesize);

        if(savedTODB){
            cFileTransfer.sendMsg(MsgType.UPLOAD,param1);
            cFileTransfer.from_C_DIR_toBuffer(new File(param1));

        }else {
            this.clientCallbackInterf.serverResponse("Operation save to database Failed");
        }
    }
    private void permission(CmdType cmd, ClientInterface clientCallbackInterf, String loggeduser,
                            String param1, String param2) throws RemoteException {
        int permission = serverInterface.checkAccessPermission(this.clientCallbackInterf,
                param1,loggeduser);
        if(permission == 0){
            this.clientCallbackInterf.serverResponse("You do not have enough permission to read this file," +
                    "Contact the file owner");
        }else if(permission == 1 ){
            if(cmd.equals(CmdType.READ)){
                serverInterface.readFile(CmdType.READ,loggeduser,clientCallbackInterf,param1);
            }else if(cmd.equals(CmdType.EDIT)){
                serverInterface.writeFile(CmdType.EDIT,loggeduser,clientCallbackInterf,
                        param1,
                        param2);
            }
        }else {
            this.clientCallbackInterf.serverResponse("Problem Retrieving file, Contact IT Administrator");
        }
    }
    private long getfileSize(String input){
        long size = 0;
        try {
            Path path = Paths.get(input);
            BasicFileAttributes attr = Files.readAttributes(path.toAbsolutePath(), BasicFileAttributes.class);
            size = attr.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * getfileOwner makes more sense if the system is accessed by users on DIFFERENT machines.
     * @param input
     * @return
     */
    private UserPrincipal getfileOwner(String input){
        UserPrincipal owner = null;
        try {
            Path path = Paths.get(input);
            FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path.toAbsolutePath(),
                    FileOwnerAttributeView.class);
            owner = ownerAttributeView.getOwner();
            System.out.println("owner: " + owner.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return owner;
    }

   public String inputHandler() throws IOException {
       System.out.println("\n Enter one of the following Commands to proceed:\n" +
               " Use <localhost>, <name and password> or <name> in options\n");
        for(CmdType cmd: CmdType.values()){
            if(cmd == CmdType.NO_COMMAND){
                continue;
            }
            System.out.println(cmd.toString() + " <option>");
        }
        this.userInput = new BufferedReader(new InputStreamReader(System.in));
        String input = userInput.readLine();
        return input;
   }

    /**
     * Passed to server through call by reference, client's callback object
     * The ONLY class that server can access by calling ClientInterface
     */
   private class ClientStub extends UnicastRemoteObject implements ClientInterface{

       public ClientStub() throws RemoteException {
           //U.R.O Handles exporting operations

       }

       @Override
       public void serverResponse(String response) throws RuntimeException {
           System.out.println("RESPONSE :" + response);
       }
       //Function to help upload file to catalog ONLY if it has successfully been saved to DB
        @Override
        public void fileStatus(int response) throws RemoteException {
            if(response  == 1){
                savedTODB = true;
            }else if(response == 0){
                savedTODB = false;
            }
        }

        @Override
        public void currentUser(String userName, String password) throws RemoteException {
            loggedInUser.add(userName);
        }

        @Override
        public void fileContents(String[] contents)throws RemoteException {
            for(String content: contents){
                System.out.println(content);
            }
        }
    }
}
