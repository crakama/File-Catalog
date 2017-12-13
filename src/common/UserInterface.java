package common;

import server.model.FileCatalogDAO;

import java.io.Serializable;

public interface UserInterface extends Serializable {


    String getName();

    String getPassword();

    String getFName();
    String getFowner();
    String getFaccessMode();
    int getFsize();


}
