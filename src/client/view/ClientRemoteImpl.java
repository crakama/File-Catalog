package client.view;

import common.ClientRemoteInterface;
import common.Credentials;
import common.ServerRemoteInterfaceStub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class ClientRemoteImpl implements Runnable {
    BufferedReader bufferedReader;
    ServerRemoteInterfaceStub sRemoteInterface;
    ClientRemoteInterface cRemoteInterface;
    int connectionId;
    public ClientRemoteImpl() throws RemoteException{

    }
    public void start(){
        new Thread(this).start();
    }

    @Override
    public void run(){
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter Host address to connect to:");
            String host = bufferedReader.readLine();
            lookupServer(host);

            initConnection();



            System.out.println("Enter your Name:");
            String name = bufferedReader.readLine();
            System.out.println("Enter your password:");
            String password = bufferedReader.readLine();

            //Invoke remote method
            //sRemoteInterface.login(cRemoteInterface, new Credentials(name,password));
            sRemoteInterface.registerUser(name,password);

        }catch (ClassNotFoundException|SQLException | NotBoundException |IOException e){
            System.out.println("Could Not Read Credentials from CMD");
            e.printStackTrace();
        }


    }

    /**
     * Get reference to the remote object
     * @param host
     * @throwsNotBoundException
     * @throwsMalformedURLException
     * @throwsRemoteException
     */
    public void lookupServer(String host) throws NotBoundException,MalformedURLException, RemoteException{
    try {
        sRemoteInterface = (ServerRemoteInterfaceStub) Naming.lookup("//" + host + "/" + ServerRemoteInterfaceStub.REGISTERED_SERVER_NAME);
        if(sRemoteInterface == null){
          System.out.println("Naming.lookup: Lookup failed. Servlet is null.");
          return;
        }
        System.out.println("Naming.lookup: Successful.");

    }catch (Exception e){
        System.out.println("Cannot connect to RMI Registry...");
    }
    }


    /**Closes the connection to the remote object. This call causes the remote server to close all
     * Open a connection to the remote object.
     * This call causes the remote server to load the JDBC driver on the server host and to
     * use that driver to establish a JDBC connection to the catalogue database.
     */
    public void initConnection() throws ClassNotFoundException, RemoteException, SQLException {
        connectionId = sRemoteInterface.openConnection();

        if(connectionId == -1){
            System.out.println(" -1 Error during OPEN db connection...");
        }
    }

    /** Passes the SQL string that was entered to the remote object for execution
     * and retrieves the result set row by row from the remote object, displaying each row as it's retrieved.
     */
    public void searchFile() throws IOException, SQLException {
        String result;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter file name to be searched:");
        try {
            // Execute the query.
          String  filename = bufferedReader.readLine();
          sRemoteInterface.searchDB(connectionId,filename );

          //Get and display the result set
            result = sRemoteInterface.getNextRow(connectionId);
            if(result == null){
                System.out.println("No file found using search criteria");
            }
            while (result != null){
                System.out.println("SEARCH RESULTS: "+result);
                result = sRemoteInterface.getNextRow(connectionId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**Closes the connection to the remote object. This call causes the remote server to close all
    * server host and database resources that are associated
    * with the JDBC connection. For example, all cursors, result sets, statement objects,
    * and connection objects are freed.
    */

    public void finalizeConnection() throws RemoteException, SQLException {
        try {
            sRemoteInterface.closeConnection(connectionId);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
