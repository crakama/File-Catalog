package common;

public class FileInfo {

    private String filename,fAccessP,fileowner;
    private int size;

    public String getFName(){
        return filename;
    }
    public String getFOwner(){
        return fileowner;
    }
    public String getFAccessP(){
        return fAccessP;
    }
    public int getFSize(){
        return size;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setfAccessP(String fAccessP) {
        this.fAccessP = fAccessP;
    }

    public void setFileowner(String fileowner) {
        this.fileowner = fileowner;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
