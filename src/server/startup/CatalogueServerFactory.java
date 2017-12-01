package server.startup;

import server.controller.ServerRemoteImplStub;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Creates an instance of the remote object, then registers it with the Registry Service.
 */
public class CatalogueServerFactory {
    private String datasource = "FileCatalog";
    private String dbms = "mysql";
    public static void main(String[] args) {
        String hostName = null;
        String hostIP = null;


        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
            hostIP = inetAddress.getHostAddress();

            System.out.println("STARTING REMOTE CATALOGUE SERVER ON..." + hostName + "[" + hostIP + "]");
        } catch (UnknownHostException e) {
            System.err.println("SERVER STARTUP EXCEPTION:Could not start the server:-" + e.getMessage());
            e.printStackTrace();
        }
        /**
         * @remoteServer Instance of the class that contains remote methods
         * @rebind add an object to RMI registry
         */
        try {
            new CatalogueServerFactory().startRegistry();

            System.out.println("SERVER IS RUNNING...");

        } catch (MalformedURLException |RemoteException e) {
            System.err.println("SERVER STARTUP EXCEPTION:Could not start the server:-" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @createRegistry, registry has to remain running so that the registry’s naming service is always available.
     * @throws RemoteException
     */
    public void startRegistry() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        }catch (RemoteException noRegistryIsRunning){
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        ServerRemoteImplStub remoteServer = new ServerRemoteImplStub(datasource, dbms);
        Naming.rebind(ServerRemoteImplStub.REGISTERED_SERVER_NAME,remoteServer);

    }
}
