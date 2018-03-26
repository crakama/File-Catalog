package com.crakama.server.controller;

import com.crakama.client.view.CmdType;
import com.crakama.common.rmi.ClientInterface;
import com.crakama.common.rmi.ServerInterface;
import com.crakama.server.model.*;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the only class(StartServer Stub) that clients can use to reach the server remotely.
 * because it was initially exported and registered at Registry
 * Has implementations similar to ServerObject(CalleeImpl)
 */
public class ServerStub extends UnicastRemoteObject implements ServerInterface{
    private final FileDao fileDao;
    private UserInterface userInterface;
    private FileInterface fileInterface;
    private Map<String,ClientInterface> monitoredFiles = new ConcurrentHashMap<>();
    /**
     * Constructor calls on superclass U.R.O to handle exporting operations
     * @throws RemoteException
     * @param dbms
     * @param datasource
     */
    public ServerStub(String dbms, String datasource) throws RemoteException {
        super();
        //TODO: Initialise FileDao here
        this.fileDao = new FileDao(dbms,datasource);
    }

    /**
     * @param clientCallbackInterf used to send notification back to client/user via ClientInterface() object
     * that was passed by client through call by reference...its client callback Object
     * @throws RemoteException
     */
    @Override
    public void register(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException {
        if(fileDao.findUserByName(name) != null){
            clientCallbackInterf.serverResponse("\nUsename already exists!!!,Please try another name\n");
        }else{
            userInterface = new User(name,password);
            String regResponse = (fileDao.registerUser(userInterface)).toString();
            clientCallbackInterf.serverResponse("REGISTRATION:  " + regResponse + " Successfully Created!!\n");
      }
    }

    @Override
    public void login(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException {
        User userObj;
        if((userObj = fileDao.findUserByName(name)) != null){
            if ((userObj.getUserName().equalsIgnoreCase(name))&& (( userObj.getPassword().equalsIgnoreCase(password) ))){
                clientCallbackInterf.serverResponse("VERIFICATION: Login of user :"+
                        userObj.getUserName()+ " with password :"+userObj.getPassword()+" was Successful!!!");
                clientCallbackInterf.currentUser(userObj.getUserName(),userObj.getPassword());

            }else if(!((userObj.getUserName().equalsIgnoreCase(name))&& (( userObj.getPassword().equalsIgnoreCase(password) )))) {
                clientCallbackInterf.serverResponse("VERIFICATION: Incorrect Credentials, Please try again!!!");
            }
            else {
                clientCallbackInterf.serverResponse("VERIFICATION: No records of such user in the system");
            }
        }
    }
    @Override
    public void unregister(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException {
        User userObj;
        if((userObj = fileDao.findUserByName(name)) != null){
            userInterface = new User(name, password);
           String userDeleted = (fileDao.deleteUser(userObj.getUserName(),userObj.getPassword())).toString();
           clientCallbackInterf.serverResponse("DE-REGISTRATION: " +userDeleted+" Successfully removed from the system");
        }else {
            clientCallbackInterf.serverResponse("DE-REGISTRATION: No such records in the system to be deleted!!!");
        }
    }

    @Override
    public void checkfile(ClientInterface clientCallbackInterf, String filename, String fowner, String accessmode, int fsize) throws RemoteException {
        if(fileDao.findFileByName(filename) != null){
            clientCallbackInterf.serverResponse("\nFile has to be Unique!!!,Please try another name\n");
        }else{
            fileInterface = new FileCatalog(filename,fowner,accessmode,fsize);
            int code = (fileDao.saveToDB(fileInterface));
            if(code == 1){
                clientCallbackInterf.fileStatus(code);
            }else{
                clientCallbackInterf.fileStatus(code);
            }

        }
    }

    @Override
    public void readFile(CmdType read, String currentUser,
                         ClientInterface clientCallbackInterf, String filename) throws RemoteException {
        String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalogAlpha\\uploads\\";
        StringBuilder filecontents = new StringBuilder();
        try {
            File file = new File(fileLocation+filename);
            Scanner filescanner = new Scanner(file);

            while(filescanner.hasNext()){
                String content = filescanner.nextLine().trim();
                filecontents.append(content);
                filecontents.append("\n");
            }
        } catch (FileNotFoundException e) {
            clientCallbackInterf.serverResponse("No such file in the system");

        }
        String[] lines = filecontents.toString().split("\n");
        clientCallbackInterf.fileContents(lines);
        notifyFileOwner(read,filename,currentUser);
    }
    private void notifyFileOwner(CmdType cmd,String filename,String currentUser) throws RemoteException {
        FileCatalog fileObj=fileDao.findFileByName(filename);
        if(((fileObj!=null))&&(!(fileObj.getOwner().equalsIgnoreCase(currentUser)))){
            fileObj.getOwner();
            for(String filekey: monitoredFiles.keySet()){
                if(filename.equalsIgnoreCase(filekey)){
                    ClientInterface obj = monitoredFiles.get(filekey);
                    obj.serverResponse(" A :"+cmd +" operation was performed on your public file:"+
                            filename+"by user :"+currentUser);
                }
            }
        }
    }

    @Override
    public void writeFile(CmdType edit, String loggeduser, ClientInterface clientCallbackInterf, String filename,
                          String filecontents) throws RemoteException {
        String fileLocation = "D:\\Projects\\IdeaProjects\\FileCatalogAlpha\\uploads\\";
        File file = new File(fileLocation+filename);
        if (!(file.exists())){
            clientCallbackInterf.serverResponse("No such file in the system");
        }else {
            try {
                FileOutputStream fout = new FileOutputStream(file,true);
                fout.write(filecontents.getBytes());
                fout.close();
                notifyFileOwner(edit,filename,loggeduser);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //TODO: Take care of if file not found
    //TODO: Future Lookup---java.io.File setWritable(boolean writable, boolean ownerOnly)
    @Override
    public int checkAccessPermission(ClientInterface clientCallbackInterf,
                                     String filename, String currentUser) throws RemoteException {
        FileCatalog fileObj=fileDao.findFileByName(filename);

        if((fileObj != null)&&(fileObj.getAccessMode().equalsIgnoreCase("private"))
                &&(fileObj.getOwner().equalsIgnoreCase(currentUser))){
            return 1;
        }else if((fileObj != null)&&(fileObj.getAccessMode().equalsIgnoreCase("public"))){
            return 1;
        }
        else if((fileObj != null)&&(fileObj.getAccessMode().equalsIgnoreCase("private"))
                &&(!(fileObj.getOwner().equalsIgnoreCase(currentUser)))) {
            return 0;
        }else {
            return 2;
        }

    }


    @Override
    public void listfiles(ClientInterface clientCallbackInterf) throws RemoteException{
        StringBuilder allfiles = new StringBuilder();
        List<FileCatalog> files = fileDao.findAllFiles();
        for(FileInterface file: files){
            allfiles.append("File: "+"Name:"+file.getFileName()+"  "+"Owner:"+file.getOwner()+"  "+
                    "AccessMode:"+file.getAccessMode()+"  "+"FileSize:"+file.getSize());
            allfiles.append("\n");
        }
        String[] lines = allfiles.toString().split("\n");
        clientCallbackInterf.fileContents(lines);

    }

    @Override
    public void fileMonitor(ClientInterface clientCallbackInterf, String filename) {
        monitoredFiles.put(filename,clientCallbackInterf);
    }

    @Override
    public void stopMonitors(ClientInterface clientCallbackInterf, List<String> monitors) {
        for(String filekey: monitoredFiles.keySet()){
            for(String monitor:monitors){
                if(monitor.equalsIgnoreCase(filekey)){
                    System.out.println("REMOVED KEY"+ filekey);
                    monitoredFiles.remove(filekey);
                }
            }
        }
    }
}
