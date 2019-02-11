package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import ca.polymtl.inf8480.tp1.shared.Fichier;

import ca.polymtl.inf8480.tp1.shared.Mail;
import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server implements ServerInterface {

	public Fichier groupList;
	private HashMap<String, String> client_logins;
	private HashMap<String, byte[]> client_ids;


	public static void main(String[] args) throws java.io.IOException {
		Server server = new Server();
		server.run();
	}

	public Server() throws java.io.IOException {
		super();
		groupList = new Fichier("server_files/group_list.txt");
		client_logins = new HashMap();
		client_ids = new HashMap();

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

	public byte[] openSession(String login, String password) throws java.io.IOException, java.rmi.server.ServerNotActiveException {
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
				client_logins.put(RemoteServer.getClientHost(), login);
				client_ids.put(RemoteServer.getClientHost(), sessionId);
			}
			i++;
		}

		return sessionId;

	}

	public byte[] getGroupList(byte[] checksum) throws java.io.IOException {

		//*
		byte[] content = null;

		//*
		if(!checksum.equals(groupList.getChecksum())){
			content = groupList.getContent();
		}

		return content;
		//*/
	}


	public int lockGroupList() throws java.rmi.server.ServerNotActiveException {

		int result = 0;
		String host = RemoteServer.getClientHost();
		//*
		if(client_ids.containsKey(host)){
			byte[] id = client_ids.get(host);
			if(groupList.getLock() == null){
				groupList.setLock(id);
				result = 1;
				// lancer un temporisateur...

			}
		}
	//*/

		return result;
	}


	public boolean pushGroupList(byte[] content) throws java.io.IOException, java.rmi.server.ServerNotActiveException {

		boolean result = Boolean.FALSE;
		String host = RemoteServer.getClientHost();

		//*
		byte[] id = client_ids.get(host);
		if(groupList.getLock().equals(id)){
			groupList.setContent(content);
			result = Boolean.TRUE;
			groupList.unlock();
		}
		//*/

		return result;
	}

	/*
	public boolean send(String subject, String address, byte[] content) throws java.rmi.server.ServerNotActiveException, java.io.IOException {
		boolean result = Boolean.FALSE;
		String host = RemoteServer.getClientHost();
		//*
		if(client_logins.containsKey(host)){
			String from = client_logins.get(host);
			Mail mail = new Mail(subject, from + "@polymtl.ca", Boolean.FALSE, new Date(), content);
			Gson gson = new GsonBuilder().create();
			//String formatMail = gson.toJson(mail);
			//System.out.println(formatMail);
			//Fichier mailFile = new Fichier("server_files/" + address.split("@")[0] +".json");

			//byte[] newContent = (new String(mailFile.getContent()) + formatMail).getBytes();
			//mailFile.writeContent(newContent);
			//result = Boolean.TRUE;
		}


		return result;
	}
	//*/
}
