package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	int getQ() throws RemoteException;
	float getM() throws RemoteException;
	int pell(int x) throws RemoteException;
	int prime(int x) throws RemoteException;
}
