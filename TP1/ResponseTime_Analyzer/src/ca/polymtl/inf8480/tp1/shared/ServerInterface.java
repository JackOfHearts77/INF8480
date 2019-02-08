package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	int execute(int a, int b) throws RemoteException;
	int execute(byte[] b) throws RemoteException;
	byte[] openSession(String l, String p) throws java.io.IOException, java.rmi.server.ServerNotActiveException;
	byte[] getGroupList(byte[] checksum) throws java.io.IOException;
	boolean pushGroupList(byte[] list) throws java.io.IOException, java.rmi.server.ServerNotActiveException;
	int lockGroupList() throws RemoteException, java.rmi.server.ServerNotActiveException;
	//void send(String subject, String dest, byte[] content);
	void send() throws java.rmi.server.ServerNotActiveException, RemoteException;


}
