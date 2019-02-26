package client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import client.ProcessedOperations;

public class Repartiteur {

	// résultat final
	private int resultat;

	// Mode du répartiteur
	private boolean secure = true;

	// Liste des serveurs disponibles
	private ArrayList<CalculusServer> serverList;

	// File des opérations à traiter, initialisé à partir du fichier que l'on appelle
	private Queue<String> operationQueue;


	public static void main(String[] args) throws java.rmi.RemoteException {
		// on démarre un compteur de temps

		Repartiteur rep = new Repartiteur();

		// On regarde les arguments, il doit y en avoir au moins 1
		// A partir du fichier donné en paramètre, on remplit la file d'opérations
		// la file est une ArrayDeque


		Queue<String> file = new ArrayDeque<>();
		file.offer("Bonjour");
		file.offer("java");
		System.out.println(file.peek());


		/*
		CalculusServer server = new CalculusServer(1, "127.0.0.1");

		System.out.println("Refus: " + server.refuseTask(3));
		System.out.println("Refus: " + server.refuseTask(8));
		System.out.println("Refus: " + server.refuseTask(13));
		System.out.println("Refus: " + server.refuseTask(30));
		System.out.println("Prime 21: " + server.processLine("prime 21"));
		System.out.println("Pell 6: " + server.processLine("pell 6"));
		ArrayList<String> ops = new ArrayList<>();
		ops.add("prime 21");
		ops.add("pell 6");
		System.out.println("process des deux résultats précédent: " + server.processTask(ops));

		//*/
	}


	public Repartiteur() {
		this.resultat = 0;

		// On récupère la liste des serveurs que l'on ajoute à serverList

		System.out.println("Mode secure: " + secure);
		// On peut éventuellement afficher les informations sur tous les serveurs
	}


	private void processQueue(){

		// tant que la file d'opérations n'est pas vide, on la traite
		while(!this.operationQueue.isEmpty()){
			processOperations(operationQueue);
			this.resultat = this.resultat % 5000;
		}
	}


	public void processOperations(Queue<String> opQ){

		Queue<Future<ProcessedOperations>> futures = splitWork(opQ);

		//tant que futures n'est pas vide
		while(!futures.isEmpty()){
			Future<ProcessedOperations> f = futures.poll();

			// si la tâche est terminée alors on traite le résultat
			if(f.isDone()){
				try{
					ProcessedOperations procOp = f.get();
					int res = procOp.getResult();
					// le serveur est en panne
					if(res == -1){
						// on remet les tâches dans la file et on supprime le serveur de la liste
					}
					// la tâche a été refusée
					else if(res == -2){
						// on remet les tâches dans la file
					}
					//
					else{

						if(this.secure){
							this.resultat += res;
						}
						else{
							// on verifie que le resultat est bon => demander a un serveur different de faire le calcul, et verifier que le resultat est identique
							// s'il ne l'est pas, on remet les tâches dans la file
						}

					}
				}
				catch(InterruptedException e){

				}
				catch(ExecutionException e){

				}


			}

			// sinon on remet la tâche à la fin de la file
			else{
				futures.offer(f);
			}

		}

	}


	public Queue<Future<ProcessedOperations>> splitWork(Queue<String> opQ){

		Queue<Future<ProcessedOperations>> futures = new ArrayDeque<>();

		int nbOpTotal = opQ.size();
		int nbOpTraitees = 0;
		int nbOpEnvoyees = 0;


		// tant que nbOpTraitees < nbOpTotal:
			// pour chaque serveur s de la liste:
				// on calcule le nombre nbOpEnvoyees d'opérations à envoyer
				// on envoie les nbOpEnvoyees premières opérations de opQ au serveur
				nbOpTraitees += nbOpEnvoyees;
				// on ajoute le resultat f d'execution dans futures


		return futures;


	}

}
