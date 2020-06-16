package com.edu.distr.sys.command.abstraction;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemote extends Remote {
  <T> T executeTask(ITask<T> t) throws RemoteException;
}
