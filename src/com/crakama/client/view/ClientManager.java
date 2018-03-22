package com.crakama.client.view;

import com.crakama.client.net.CFileTransfer;
import com.crakama.common.ClientInterface;
import com.crakama.common.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientManager implements Runnable{
    ServerInterface serverInterface;
    //Thread fileworker;
    private final String host = "localhost";
    private final int port = 1213;
    private boolean commandsReceived = false;
    private boolean loginsession = false;
    private BufferedReader userInput;
    private final ClientInterface clientCallbackInterf;
    //private final CFileTransfer clientFileHandler;
    public ClientManager() throws RemoteException {
        clientCallbackInterf = new ClientStub();
        //clientFileHandler = new CFileTransfer();
        //fileworker = new Thread(clientFileHandler);
    }

    public void start(ServerInterface serverInterface) {
        this.serverInterface = serverInterface;
        commandsReceived = true;
        new Thread(this).start();
    }

    /**
     * Inteprate CMD commands read and invoke necessary methods
     */
    public void run() {
        while(commandsReceived){
            try {
                CmdReader cmdReader = new CmdReader(inputHandler());
                switch (cmdReader.getCmd()){
                    case LOGIN:
                        serverInterface.login(clientCallbackInterf,
                                cmdReader.getParameters(1),
                                cmdReader.getParameters(2));
                        loginsession = true;
                    break;
                    case LOGOUT:
                        loginsession = false;
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
                            new CFileTransfer().start(host,port,clientCallbackInterf,
                                    cmdReader.getCmd(), cmdReader.getParameters(1));
                        }else {
                            clientCallbackInterf.serverResponse("You need to Register and " +
                                    "Login to View the file");
                        }
                        break;
                    case UPLOAD:
                        if(loginsession==true){
                            serverInterface.checkfile(clientCallbackInterf,
                                    cmdReader.getParameters(1));

//                            new CFileTransfer().start(host,port,clientCallbackInterf,
//                                    cmdReader.getCmd(),cmdReader.getParameters(1));
                        }else {
                            clientCallbackInterf.serverResponse("You need to Register and " +
                                    "Login to Upload the file");
                        }
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


   public String inputHandler() throws IOException {
       System.out.println("Enter one of the following Commands to proceed:\n" +
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
           System.out.println("StartServer Response :" + response);
       }
   }
}
