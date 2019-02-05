package ca.polymtl.inf8480.tp1.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import ca.polymtl.inf8480.tp1.shared.Fichier;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;


public class Client {
	public static void main(String[] args) throws java.io.IOException {

		Client client = new Client();
		boolean connected = client.verify();

		if(!connected){
			client.handleConnexion(args);
		}

		else{
			client.handleArgs(args);
		}
	}

	FakeServer localServer = null;
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;
	// byte[] id;
;

	public Client() {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");
		//distantServerStub = loadServerStub("132.207.89.183");
		distantServerStub = loadServerStub("127.0.0.1");



	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
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

	/*
		On vérifie que le fichier connect.sess existe et est non vide.
		Ceci signifie que le client est connecté.
		Son identifiant de session se trouve dans le fichier.
	 */
	private boolean verify(){
		String filePath = Paths.get("").toAbsolutePath().toString() + "/connect.sess";
		File connexionFile = new File(filePath);

		boolean resultat;

		if(connexionFile.exists() && connexionFile.length() > 0){
			//System.out.println("Fichier non vide");
			resultat = Boolean.TRUE;
		}

		else{
			//System.out.println("Fichier vide ou non existant");
			resultat = Boolean.FALSE;
		}


		return resultat;
	}


	private void handleConnexion(String args[]) throws java.io.IOException {
		//vérifier l'existence des arguments aussi....
		if(args[0].equals("connect")){
			if(args.length == 3){

				byte[] sessionId = distantServerStub.openSession(args[1], args[2]);

				// si sessionId est null, on dit que le login ou le mdp est faux
				if(sessionId != null){
					String currentDirectory = Paths.get("").toAbsolutePath().toString();
					Path filePath = Paths.get(currentDirectory + "/connect.sess");
					Files.write(filePath, sessionId);
				}
				else{
					System.out.println("Login ou mot de passe incorrect !");
				}


			}
			else{
				System.out.println("Vous devez spécifier un login et un mot de passe !");
			}
		}
		else{
			System.out.println("Vous devez d'abord vous connecter !");
		}
	}


	private void updateGroupList() throws java.io.IOException {

		Fichier groupList = new Fichier("client_files/group_list.txt");

		byte[] newContent = distantServerStub.getGroupList(groupList.getChecksum());
		if (newContent != null){
			groupList.writeContent(newContent);
			System.out.println("Le fichier de groupe de liste a été mis a jour !");
		}
		else{
			System.out.println("Le fichier de groupe de liste est déja a jour !");
		}
	}


	private void lockGroupListClient(){
		int result = distantServerStub.lockGroupList();

		if (result == 1){
			System.out.println("La liste des groupes a bien été vérouillée !");
		}
		else{
			System.out.println("La liste des groupes est déjà vérouillée par un autre utiliateur !");
		}
	}

	private void publish() throws java.io.IOException {

		Fichier groupList = new Fichier("client_files/group_list.txt");
		byte[] newContent = distantServerStub.getGroupList(groupList.getChecksum());

		if(newContent != null){
			boolean modified = distantServerStub.pushGroupList(groupList.getContent());

			if(modified){
				System.out.println("La liste des groupes a bien été modifiée sur le serveur !");
			}
			else{
				System.out.println("Vous devez vérouiller la liste globale !");
			}
		}
		else{
			System.out.println("La liste du groupe du serveur est déjà identique à la votre !");
		}

	}


	private void handleArgs(String args[]) throws java.io.IOException {
		if(args.length ==0){
			System.out.println("Saisissez un argument");
		}
		else{
			switch(args[0]){
				case("connect"):
					System.out.println("Vous êtes déjà connecté !");
					break;

				case("get-group-list"):
					System.out.println("On veut la liste des groupes");
					updateGroupList();
					break;

				case("publish-group-list"):
					System.out.println("On push la liste des groupes locales");
					publish();
					break;

				case("lock-group-list"):
					System.out.println("On lock le fichier de groupes du serveur pour le modifier");
					lockGroupListClient();
					break;

				case("send"):
					System.out.println("Envoi d'un mail");
					break;

				case("list"):
					System.out.println("On renvoie la liste des mails");
					break;

				case("read"):
					System.out.println("Lecture d'un mail");
					break;

				case("delete"):
					System.out.println("On supprime un mail");
					break;

				case("search"):
					System.out.println("On cherche un mail par mot clé");
					break;

				default:
					System.out.println("Saisissez une commande valide !!");
					break;
			}
		}
	}

}
