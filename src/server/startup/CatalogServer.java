package server.startup;

import server.controller.FileServerImpl;
import server.controller.ServerRMIImpl;
import server.controller.ServerTCPImpl;
import server.model.FileCatalogDAO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * Creates an instance of the remote object, then registers it with the Registry Service.
 */
public class CatalogServer {
    private static String datasource = "filecatalog";
    private static String dbms = "mysql";
    private static final int port=1234;
    private static final int fileport=1235;
    private static ServerSocket serverSocket,fserverSocket;
     String filename,owner,accessPerm;
    int size;

    public CatalogServer(){ }

   public CatalogServer(String filename, int size, String owner, String accessPerm) {
       this.filename = filename;
       this.size = size;
       this.owner = owner;
       this.accessPerm = accessPerm;
   }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /**
         * @CatalogServer() Creates a new Instance of the class object(server) which implements
         * @startRegistry() method.
         *
         * @ServerTCPImpl().start() an new instance of a different server object is started
         * The class implements methods that client can access over TCP connections and not RMI
         */
        try {
            new CatalogServer().startRegistry();
            System.out.println("RMI SERVER IS RUNNING...");

        } catch (MalformedURLException |RemoteException e) {
            System.err.println("SERVER STARTUP EXCEPTION:Could not start the server:-" + e.getMessage());
            e.printStackTrace();
        }
        new CatalogServer().initTCPConnection();

    }




    /**
     * @getRegistry() Obtain a reference to remote object registry at localhost, if it doesn't exist,
     * @createRegistry, create a new one with a given port number
     * registry has to remain running so that the naming service is always available to clients.
     * @rebind A call to the registry through OOM to install server class object(Stub) at the registry.
     */
    public void startRegistry() throws RemoteException, MalformedURLException, SQLException, ClassNotFoundException {
        try {
            LocateRegistry.getRegistry().list();
        }catch (RemoteException noRegistryIsRunning){
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        ServerRMIImpl remoteServer = new ServerRMIImpl(datasource, dbms);
        Naming.rebind(ServerRMIImpl.REGISTERED_SERVER_NAME,remoteServer);

    }

    public void initTCPConnection(){
        try {
            System.out.println("TCP SERVER IS RUNNING...");
            serverSocket = new ServerSocket(port);
            System.out.println("server socket acquired\n");

            fserverSocket = new ServerSocket(fileport);
            System.out.println("file server socket acquired\n");

            while(true){
                //Connection for user commands
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server accepted socket connection\n");
                tcpClientsManager(clientSocket);

                //Connection for file transfer
                Socket fClientSocket = fserverSocket.accept();
                System.out.println("Server accepted file transfer socket connection\n");
                tcpFileManager(fClientSocket);
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to port:" + port + "Already used!!!");
            e.printStackTrace();

        }catch (Exception e){
            //Catch any other Exception and print it to the console
            System.out.println(e.toString());
        }

    }

    public void tcpFileManager(Socket clientSocket){

        //use getters to read data sent by user

        FileServerImpl serverTCP = new FileServerImpl(clientSocket);
        Thread serverThread = new Thread(serverTCP);

        System.out.println("New Server Thread Started on socket:" + clientSocket);
        serverThread.start();
    }

    /**
     * Pass client socket to the server thread
     * @param clientSocket
     */
    public void tcpClientsManager(Socket clientSocket){
        ServerTCPImpl serverTCP = new ServerTCPImpl(clientSocket);
        Thread serverThread = new Thread(serverTCP);

        System.out.println("New Server Thread Started on socket:" + clientSocket);
        serverThread.start();
    }
}
