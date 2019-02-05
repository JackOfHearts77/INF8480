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

	private Fichier groupList;

	public static void main(String[] args) throws java.io.IOException {
		Server server = new Server();
		server.run();
	}

	public Server() throws java.io.IOException {
		super();
		Fichier groupList = new Fichier("server_files/group_list.txt");
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
					// ajouter l'id dans un ArrayMap et crée un ClientInterface
				}
				i++;
			}

			return sessionId;

	}

	public byte[] getGroupList(byte[] checksum) throws java.io.IOException {

		byte[] content = null;

		if(!checksum.equals(groupList.getChecksum())){
			content = groupList.getContent();
		}

		return content;

	}


	public int lockGroupList(){

		int result = 0;

		if(!groupList.getLock()){
			groupList.setLock();
			result = 1;
			// lancer un temporisateur...

		}

		return result;
	}


	public boolean pushGroupList(byte[] content) throws java.io.IOException {

		boolean result = Boolean.FALSE;

		if(groupList.getLock()){
			groupList.setContent(content);
			result = Boolean.TRUE;
			groupList.unlock();
		}

		return result;
	}

	public void send(String subject, String dest, byte[] content){
		//id = client.getId()
		// name = clients[id]

	}

}
