package client.view;

import common.ServerRMIInterface;
import server.model.UserImpl;
import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class ClientRemoteImpl implements Runnable {
    BufferedReader bufferedReader;
    ServerRMIInterface sRemoteInterface;
    Socket clientlink = null;
    //private InetAddress host;
    private final int port=1234;
    String host = null;
    int connectionId;
    private boolean usercommandrcvd = false;
    public ClientRemoteImpl() {

    }
    public void start(){
        if(usercommandrcvd){
            return;
        }
        usercommandrcvd= true;
        new Thread(this).start();
    }

    @Override
    public void run(){
        UserImpl userInterface = null;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("You need to be connected to a remote server first!!!" +
                " %n Enter Host address to connect to:");
        try {
            host = bufferedReader.readLine();
            lookupServer(host);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while(usercommandrcvd){
            try{
                System.out.println("Enter Command to start the program:");
                String readBytes = bufferedReader.readLine();
                switch (readBytes){

                    case "register":
                        System.out.println("You can now proceed with registration:");
                        System.out.println();
                        System.out.println("Enter your Name:");
                        String name = bufferedReader.readLine();
                        System.out.println("Enter your password:");
                        String password = bufferedReader.readLine();
                        //Invoke remote method
                        sRemoteInterface.registerUser(name,password);
                        System.out.println("You Successfully registered as a new user !!!:");
                        break;
                    case "upload":
                        System.out.println("Enter file name e.g hw.pdf or code.jpg");
                        String filename = bufferedReader.readLine();
                        sendFile(filename);

                        break;
                    case "unregister":
                        System.out.println("Enter username to unregister:");
                        String unreguser = bufferedReader.readLine();
                        if(unreguser == "n"){
                            System.out.println("Enter Command to continue with the program:");
                        }
                        userInterface = sRemoteInterface.getUserRecord(unreguser);
                        sRemoteInterface.unRegisterUser(userInterface);

                    default:
                        //System.out.println("You typed an invalid command");
                }
            }catch (SQLException |IOException e){
                System.out.println("Could Not Read Credentials from CMD");
                e.printStackTrace();
            }
        }


    }
    /**
     * ObjectOutputStream object to Send request to server using TCP socket
     * @param filename
     */

    public void sendFile(String filename) throws IOException {
        try {
            clientlink = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(clientlink.getOutputStream());
            FileInputStream fInputStream = new FileInputStream(filename);

            //Convert file into bytes of array
            long filelen = (new File(filename)).length();
            int intfilen = (int)filelen;
            byte[] filedata = new byte[intfilen];

            //Read bytes into memory
            fInputStream.read(filedata);
            fInputStream.close();

            //write byte array to socket
            out.writeObject(filedata);
            out.flush();

            out.close();
            fInputStream.close();
        } catch (IOException e) {
            System.out.println("Host Address Not Found:");
            e.printStackTrace();
        }

    }

    /**
     * Client gets reference to the remote object
     * @ServerRemoteInterfaceStub.REGISTERED_SERVER_NAME is the label of the required object
     * @param host
     */
    public void lookupServer(String host) {
    try {
        sRemoteInterface = (ServerRMIInterface) Naming.lookup("//" + host + "/" + ServerRMIInterface.REGISTERED_SERVER_NAME);
        if(sRemoteInterface == null){
          System.out.println("Naming.lookup: Lookup failed. sRemoteInterface is null.");
          return;
        }
        System.out.println("NAMING LOOKUP STATUS: Successful.");

    }catch (Exception e){
        System.out.println("FAILED TO GET OBJECT REFERENCE AT RMI REGISTRY...");
    }
    }


    /**Closes the connection to the remote object. This call causes the remote server to close all
     * Open a connection to the remote object.
     * This call causes the remote server to load the JDBC driver on the server host and to
     * use that driver to establish a JDBC connection to the catalogue database.
     */
    public void initConnection() throws ClassNotFoundException, RemoteException, SQLException {
        sRemoteInterface.openConnection();

     /*   if(connectionId == -1){
            System.out.println(" -1 Error during OPEN db connection...");
        }*/
    }

    /** Passes the SQL string that was entered to the remote object for execution
     * and retrieves the result set row by row from the remote object, displaying each row as it's retrieved.
     */
    public void searchFile() throws SQLException {
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

    public void finalizeConnection() {
        try {
            sRemoteInterface.closeConnection(connectionId);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
