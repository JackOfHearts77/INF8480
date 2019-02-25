package client;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by jelacs on 25/02/19.
 */
public class Thread implements Callable {

    private ArrayList<String> operations;
    private CalculusServer server;

    public Thread(ArrayList<String> operations, CalculusServer server){
        this.operations = operations;
        this.server = server;
    }

    public ProcessedOperations call(){
        return new ProcessedOperations(this.server.processTask(operations), server.getId(), operations);
    }

}
