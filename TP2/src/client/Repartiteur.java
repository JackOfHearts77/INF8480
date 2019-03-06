package client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import client.ProcessedOperations;

public class Repartiteur {

	// résultat final
	private int resultat;

	// Mode du répartiteur
	private boolean secure;

	// Liste des serveurs disponibles
	private ArrayList<CalculusServer> serverList;

	// File des opérations à traiter, initialisé à partir du fichier que l'on appelle
	private Queue<String> operationQueue;


	public static void main(String[] args) throws IOException {
		// on démarre un compteur de temps
		long begin_time = System.currentTimeMillis();

		// On crée un répartiteur et on affiche ses caractéristiques
		Repartiteur rep = new Repartiteur();
		rep.toString();

		// On regarde les arguments, il doit y en avoir au moins 1 arguments
		if(args.length > 0){

			// on lit le fichier d'opérations et on ajoute chaque ligne dans la file
			try {
				File file = new File(Paths.get("").toAbsolutePath().toString() + "/fichiers/" + args[0]);
				Scanner scan = new Scanner(file);

				while(scan.hasNextLine()){
					rep.operationQueue.offer(scan.nextLine());
				}

				scan.close();
				rep.processQueue();

			} catch(IOException e){
				e.printStackTrace();
			}

		}

		System.out.println("Résultat : " + (rep.resultat));
		System.out.print("Temps execution: ");
		System.out.println(System.currentTimeMillis() - begin_time + " ms");
		System.exit(0);


		/*
		Queue<String> queue = new ArrayDeque<>();
		queue.offer("Bonjour");
		queue.offer("java");
		System.out.println(queue.peek());
		//*/

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
		this.operationQueue = new ArrayDeque<>();


		// On récupère la liste des serveurs que l'on ajoute à serverList
		this.serverList = new ArrayList<>();
		try {
			File file = new File(Paths.get("").toAbsolutePath().toString() +"/config.ini");
			Scanner scan = new Scanner(file);
			// la première ligne contient le mode
			if(scan.hasNext()){
				if(scan.nextLine().equals("true")){
					this.secure = true;
				}
				else{
					this.secure =false;
				}
			}

			// les autres lignes contiennent les adresses ip
			int i = 0;
			while (scan.hasNextLine()){
				this.serverList.add(new CalculusServer(i, scan.nextLine()));
				i++;
			}
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public String toString(){
		String newLine = System.getProperty("line.separator");
		String s = "*** mode "+ this.secure+ " ***" + newLine;
		for(CalculusServer server : this.serverList){
			s = s + server.toString() + newLine;
		}

		return s;
	}


	private void processQueue(){

		// tant que la file d'opérations n'est pas vide, on la traite
		while(!this.operationQueue.isEmpty()){
			processOperations(operationQueue);
			//System.out.println("résultat courant: " + this.resultat);
			//System.out.println("file après appel: " + operationQueue.toString());
		}
	}


	private void processOperations(Queue<String> opQ){

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
						addToOperationQueue(procOp.getOperations());
						removeServer(procOp.getServerId());
					}
					// la tâche a été refusée
					else if(res == -2){
						addToOperationQueue((procOp.getOperations()));
					}
					//
					else{

						if(this.secure){
							this.resultat += res;
							this.resultat = this.resultat % 5000;
						}
						else{
							// on verifie que le resultat est bon => demander a un serveur different de faire le calcul, et verifier que le resultat est identique
							boolean goodResult = verify(procOp.getOperations(), procOp.getResult(), procOp.getServerId());
							//System.out.println("Nombre d'opérations: " + procOp.getOperations().size() + "bon résultat: " + goodResult);
							if(goodResult){
								this.resultat += res;
								this.resultat = this.resultat % 5000;
							}
							// s'il ne l'est pas, on remet les tâches dans la file
							else{
								addToOperationQueue(procOp.getOperations());
							}
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

	private void addToOperationQueue(ArrayList<String> ops){

		for(String op : ops){
			this.operationQueue.offer(op);
		}

	}


	private void removeServer(int id){
		int i = 0;
		boolean removed = false;

		while(i < this.serverList.size() && !removed){
			if(serverList.get(i).getId() == id){
				serverList.remove(i);
				removed = true;
			}
		}
	}


	// On découpe le travail de la façon suivante, on envoie un nombre n de tâches qui soit tel que le
	// refus du serveur ne dépasse pas 10%

	private Queue<Future<ProcessedOperations>> splitWork(Queue<String> opQ){

		Queue<Future<ProcessedOperations>> futures = new ArrayDeque<>();

		ExecutorService executor = Executors.newFixedThreadPool(this.serverList.size());


		int nbOpTotal = opQ.size();
		int nbOpTraitees = 0;
		int i = 0;


		// tant que nbOpTraitees < nbOpTotal:
		while(i < this.serverList.size() && nbOpTraitees < nbOpTotal){

			CalculusServer s = this.serverList.get(i);


			// on récupère le min entre le nombre d'opérations qui donne un taux de refus de 10% et le nombre d'opération restante
			int n = Math.min((int) Math.floor(1.5 * (float) s.getQ()), nbOpTotal - nbOpTraitees);

			// On récupère les n premières opérations
			ArrayList<String> n_ops = new ArrayList<>();

			for(int j = 0; j < n; j++){
				n_ops.add(opQ.poll());
			}

			// On lance le processus
			Callable<ProcessedOperations> thread = new Thread(n_ops, s);
			Future<ProcessedOperations> f = executor.submit(thread);

			// on ajoute la tâche à la liste des tâches envoyées
			futures.add(f);

			// on met à jour le nombre d'opération traitées
			nbOpTraitees += n;

		}

		return futures;

	}



	private boolean verify(ArrayList<String> ops, int res, int serverId){

		// Si jamais la boucle dure trop longtemps c'est qu'il n'y a pas de serveur avec la bonne capacité
		//long begin = System.currentTimeMillis();
		//int max_wait = 10000;

		//
		boolean equal = false;

		int newRes = -3;

		while(newRes < 0 ){
			int newId = serverId;


			while(newId == serverId){
				// on génère un entier entre 0 et le nombre de serveur (non inclus)
				newId = (int) (Math.random() * this.serverList.size());
			}

			int capacity = (int) Math.floor(1.5 * (float) this.serverList.get(newId).getQ());

			if(ops.size() <= capacity){
				ExecutorService executor = Executors.newFixedThreadPool(this.serverList.size());

				Callable<ProcessedOperations> thread = new Thread(ops, this.serverList.get(newId));
				Future<ProcessedOperations> f = executor.submit(thread);


				try {
					newRes = f.get().getResult();
					if ( newRes == res) {
						equal = true;
					}
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		//System.out.println("nouveau résultat: " + newRes + " et vérification ok: " + equal);
		return equal;
	}

}
