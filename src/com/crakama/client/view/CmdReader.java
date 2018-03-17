package com.crakama.client.view;

class CmdReader {
    private final String userCommand;
    CmdType cmd;
    private String [] requestToken;
    CmdReader(String userCommand){
        this.userCommand = userCommand;
        readCommand(userCommand);
        readParameter(userCommand);
    }

    public void readCommand(String userCommand){
        try {
            String[] cmdToken = deleteExtraSpaces(userCommand).split(" ");
            cmd = CmdType.valueOf(cmdToken[0].toUpperCase());
        }catch (Throwable failedtoReadCommand){
            cmd = CmdType.NO_COMMAND;
        }
    }

    public void readParameter(String userInput){
        try {
            this.requestToken = deleteExtraSpaces(userInput).split(" ");

        }catch (Throwable failedtoReadCommand){

        }
    }

    private String deleteExtraSpaces(String source) {
        if (source == null) {
            return source;
        }
        String oneOrMoreOccurences = "+";
        return source.trim().replaceAll(" " + oneOrMoreOccurences, " ");
    }
    public String getParameters(int index){
        if(requestToken == null){
            return null;
        }
        if(index >= requestToken.length){
            return null;
        }

        return requestToken[index];
    }
    CmdType getCmd(){
        return cmd;
    }

    String getUserInput(){
        return userCommand;
    }


}
