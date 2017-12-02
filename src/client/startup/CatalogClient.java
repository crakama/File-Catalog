package client.startup;

import client.view.ClientRemoteImpl;

public class CatalogClient {
    public static void main(String[] args ){
        try {
            new ClientRemoteImpl().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
