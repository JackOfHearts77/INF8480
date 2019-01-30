package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import ca.polymtl.inf8480.tp1.shared.Fichier;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
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

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	@Override
	public int execute(int a, int b) throws RemoteException {
		return a + b;
	}

	@Override
	public int execute(byte[] b) throws RemoteException {
		return b.length;
	}


	public byte[] generateId(){

		String uniqueID = UUID.randomUUID().toString();
		System.out.println("New client id "+uniqueID);
		return uniqueID.getBytes();
	}

	public byte[] openSession(String login, String password) throws java.io.IOException {
			Fichier users = new Fichier("server_files/users_list.txt");

			String[] ids = (new String(users.getContent())).toString().split("\n");
			byte[] sessionId = null;
			int i = 0;
			boolean verified = Boolean.FALSE;

			while(i < ids.length && !verified){
				String[] id = ids[i].split(" ");
				if(id[0].equals(login) && id[1].equals(password)){
					verified = Boolean.TRUE;
					sessionId = generateId();
				}
				i++;
			}

			return sessionId;

	}

}
