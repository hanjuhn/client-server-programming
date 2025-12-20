/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Framework;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIEventBus extends Remote {
	public long register() throws RemoteException;
	public void unRegister(long SenderID) throws RemoteException;
	// event m 값이 달라지면서 구별되는 방식 
	public void sendEvent(Event m ) throws RemoteException;
	public EventQueue getEventQueue(long SenderID) throws RemoteException;
}
