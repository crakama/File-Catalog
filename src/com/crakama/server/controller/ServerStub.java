package com.crakama.server.controller;

import com.crakama.common.ClientInterface;
import com.crakama.common.ServerInterface;
import com.crakama.server.model.FileDao;
import com.crakama.server.model.User;
import com.crakama.server.model.UserInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This is the only class(StartServer Stub) that clients can use to reach the server remotely.
 * because it was initially exported and registered at Registry
 * Has implementations similar to ServerObject(CalleeImpl)
 */
public class ServerStub extends UnicastRemoteObject implements ServerInterface{
    private final FileDao fileDao;
    private UserInterface userInterface;

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
}
