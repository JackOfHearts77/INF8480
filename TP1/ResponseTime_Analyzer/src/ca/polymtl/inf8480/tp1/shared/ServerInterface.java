package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	int execute(int a, int b) throws RemoteException;
	int execute(byte[] b) throws RemoteException;
	byte[] openSession(String l, String p) throws java.io.IOException;
	//byte[] getGroupList(byte[] checksum);
	//void pushGroupList(byte[] list);
	//int lockGroupList();
	//int sendMail(String subject, String dest, byte[] content);


}
