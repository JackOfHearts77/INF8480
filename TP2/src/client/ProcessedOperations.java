package client;

import java.util.ArrayList;

/**
 * Created by jelacs on 25/02/19.
 */
public class ProcessedOperations {

    private int result;
    private int serverId;
    private ArrayList<String> operations;


    public ProcessedOperations(int result, int serverId, ArrayList<String> o){
        this.result = result;
        this.serverId = serverId;
        this.operations = o;
    }

    public int getResult() {
        return result;
    }

    public int getServerId() {
        return serverId;
    }

    public ArrayList<String> getOperations() {
        return operations;
    }


    @Override
    public String toString() {
        return "ProcessedOperations{" +
                "result=" + result +
                ", serverId=" + serverId +
                ", operations=" + operations.toString() +
                '}';
    }

}
