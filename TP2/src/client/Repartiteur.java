package client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Future;

import shared.ServerInterface;

public class Repartiteur {

	// résultat final
	private int resultat;

	// Mode du répartiteur
	private boolean secure = true;

	// Liste des serveurs disponibles
	private ArrayList<CalculusServer> serverList;

	// File des opérations à traiter, initialisé à partir du fichier que l'on appelle
	private Queue<String> operationQueue;


	public static void main(String[] args) {
		// on démarre un compteur de temps

		Repartiteur rep = new Repartiteur();

		// On regarde les arguments, il doit y en avoir au moins 1
		// A partir du fichier donné en paramètre, on remplit la file d'opérations

	}


	public Repartiteur() {
		this.resultat = 0;

		// On récupère la liste des serveurs que l'on ajoute à serverList

		System.out.println("Mode secure: " + secure);
		// On peut éventuellement afficher les informations sur tous les serveurs
	}


	private void processQueue(){

		//tant que operationQueue n'est pas vide
			processOperations(operationQueue);
			this.resultat = this.resultat % 4000;

	}


	public void processOperations(Queue<String> opQ){

		Queue<Future<ProcessedOperations>> futures = splitWork(opQ);

		//tant que futures n'est pas vide
			// f = futures.peek()
			// si f est terminé on traite le résultat (panne, refus, faux résultat)
				// si on a un bon résultat on l'ajoute
				// sinon on ajoute les opérations de f à this.operationQueue


	}


	public Queue<Future<ProcessedOperations>> splitWork(Queue<String> opQ){

		Queue<Future<ProcessedOperations>> futures = new ArrayDeque();

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
