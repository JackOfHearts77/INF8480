package client;

import shared.ServerInterface;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jelacs on 25/02/19.
 */
public class CalculusServer {

    private int id;
    private String ip;
    private ServerInterface stub;
    private int q;
    private float m;
    private boolean isWorking;

    public CalculusServer(int id, String ip){
        this.id = id;
        this.ip = ip;
        this.m = 0;
        this.stub = loadServerStub(ip);
        this.isWorking = true;
    }


    private ServerInterface loadServerStub(String hostname) {
        ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, 5021);
            stub = (ServerInterface) registry.lookup("server");
            setQ(stub.getQ());
            setM(stub.getM());
        } catch (NotBoundException e) {
            System.out.println("Erreur: Le nom '" + e.getMessage()
                    + "' n'est pas défini dans le registre.");
        } catch (AccessException e) {
            System.out.println("Erreur: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        return stub;
    }

    // méthode qui permet de décider si on refuse la tâche ou non selon la formule de l'énoncé
    private boolean refuseTask(int taskLength){

        float refus = ((float) taskLength - this.getQ())/(5*this.getQ());

        Random rnd = new Random();
        float value = rnd.nextFloat();

        if (value < refus){
            return true;
        }
        else {
            return false;
        }

    }

    // permet de calculer le résultat de l'ensemble des opérations
    // on doit d'abord déterminer si la tâche est refusée ou si le serveur est en panne
    // on doit également prendre en compte si on renvoie un résultat correct ou non
    public int processTask(ArrayList<String> tasks) throws java.rmi.RemoteException {

        int result = 0;

        if(!isWorking){
            result = -1;
        }

        else if(tasks.size() > this.stub.getQ() && refuseTask(tasks.size())){
            result = -2;
        }

        else{
            for(String t : tasks){
                result += processLine(t);
                result = result % 5000;
            }
        }

        return result;
    }

    // on calcule le résultat pour une seule opération de la liste
    private int processLine(String task) throws java.rmi.RemoteException {

        int result = 0;

        //on split task: op et arg
        // appel rmi sur op avec l'argument arg
        String[] t = task.split(" ");

        if(t[0].equals("pell")){
            result = this.stub.pell(Integer.parseInt(t[1]));
        }
        else{
            result = this.stub.prime(Integer.parseInt(t[1]));
        }

        if(!this.stub.resultIsCorrect()){
            Random rnd = new Random();
            result =  rnd.nextInt();
        }

        return result;
    }



    public int getId(){

        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ServerInterface getStub() {
        return stub;
    }

    public void setStub(ServerInterface stub) {
        this.stub = stub;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public float getM() {
        return m;
    }

    public void setM(float m) {
        this.m = m;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    // deux serveurs sont égaux ???

    //toString à Overide pour obtenir les informations sur le serveur de calcul
    @Override
    public String toString(){

        String newLine = System.getProperty("line.separator");
        String s = "*** server "+ this.id+ " ***" + newLine +
                "ip :"+ this.ip + newLine +
                "q :"+ this.q + newLine;

        return s;
    }
}
