package com.crakama.server.model;

public class FileEventsImpl implements FileEvents {
    private String filename,fileDIR,event,accessor;

    public FileEventsImpl(String fileDIR,String filename, String accessor){
        this.filename = filename;
        this.fileDIR = fileDIR;
        this.event = "Null at FEI";
        this.accessor = accessor;
    }

    public FileEventsImpl() {
        this(null,null,null);
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setFileDIR(String fileDIR) {
        this.fileDIR = fileDIR;
    }
    public void setAccessor(String event) {
        this.event = event;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public String getDir() {
        return fileDIR;
    }

    @Override
    public String getAccessor() {
        return accessor;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(", Directory: [");
        builder.append(fileDIR);
        builder.append(", File: ");
        builder.append(filename);
        builder.append(", Accessor: ");
        builder.append(accessor);
        builder.append("]");
        return builder.toString();
    }
}
