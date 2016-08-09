package com.qinjiangbo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Richard on 7/2/16.
 */
public interface OnlineComputer extends Remote {

    public int add(int num1, int num2) throws RemoteException;

    public int subtract(int num1, int num2) throws RemoteException;

    public float divide(int num1, int num2) throws RemoteException;

    public int multiply(int num1, int num2) throws RemoteException;

}
