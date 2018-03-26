package com.crakama.client.view;

import com.crakama.client.net.CFileTransfer;
import com.crakama.common.rmi.ClientInterface;
import com.crakama.common.rmi.ServerInterface;
import com.crakama.common.tcp.MsgType;
import com.crakama.server.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
    Queue<String> loggedInUser = new ConcurrentLinkedQueue<>();
    public ClientManager() throws RemoteException {

    }

    public void start(ServerInterface serverInterface) {
        try {
        this.serverInterface = serverInterface;
        commandsReceived = true;
        cFileTransfer = new CFileTransfer();
        clientCallbackInterf = new ClientStub();
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

                        }else {
                            clientCallbackInterf.serverResponse("You need to Register and " +
                                    "Login to View the file");
                        }
                        break;
                    case UPLOAD:
                        //TODO: Take care of same column name for file and user tables
                        if(loginsession==true){

                            serverInterface.checkfile(clientCallbackInterf,
                                    cmdReader.getParameters(1),"kate",cmdReader.getParameters(2),50);
                            //long filesize = getfileSize(input);
                            if(savedTODB){
                                cFileTransfer.sendMsg(MsgType.UPLOAD,cmdReader.getParameters(1));
                                cFileTransfer.from_C_DIR_toBuffer(new File(cmdReader.getParameters(1)));

                            }else {
                                clientCallbackInterf.serverResponse("Operation save to database Failed");
                            }

                        }else {
                            clientCallbackInterf.serverResponse("You need to Register and " +
                                    "Login to Upload the file");
                        }
                        break;
                    case READ:
                        serverInterface.readFile(clientCallbackInterf,cmdReader.getParameters(1));
                        break;
                    case WRITE:
                        serverInterface.writeFile(clientCallbackInterf,
                                cmdReader.getParameters(1),
                                cmdReader.getParameters(2));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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
