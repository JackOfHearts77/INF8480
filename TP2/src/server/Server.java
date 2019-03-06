package server;

import shared.Operations;
import shared.ServerInterface;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;


public class Server implements ServerInterface {

	// capacité du serveur
	private int q;

	// probabilité de malice du serveur
	private float m;

	//taux de refus
	private float t;


	public static void main(String[] args) {

		if(args.length < 2){
			System.out.println("Saisissez un nombre d'arguments valide: ./server.sh q m");
		}
		else if (Integer.parseInt(args[0]) < 0){
			System.out.println("Entrez une valeur q (capacité du serveur) qui est positive !");
		}
		else if(Float.parseFloat(args[1]) < 0){
			System.out.println("Entrez une valeur m (malice du serveur) qui est positive !");
		}
		else {
			Server server = new Server(Integer.parseInt(args[0]), Float.parseFloat(args[1]));
			server.run();
		}


	}

	public Server(int q, float m) {
		super();
		this.q = q;
		this.m = m/100;

	}

	public int getQ() throws RemoteException {
		return q;
	}

	public float getM() throws RemoteException{
		return m;
	}

	// TODO -- Eventuellement changer les ports de connexions !!!
	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 5020);

			Registry registry = LocateRegistry.getRegistry(5021);
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}


	@Override
	public int pell(int x) throws RemoteException {
		return Operations.pell(x);
	}

	@Override
	public int prime(int x) throws RemoteException {
		return Operations.prime(x);
	}

	// détermine selon la valeur de m, si la réponse envoyée par le serveur doit être malicieuse ou non
	public boolean resultIsCorrect() {
		Random rnd = new Random();
		float value = rnd.nextFloat();

		if(value < m){
			return false;
		}
		else {
			return true;
		}

	}
}
