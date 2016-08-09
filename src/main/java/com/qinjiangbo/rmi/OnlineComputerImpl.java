package com.qinjiangbo.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Richard on 7/2/16.
 */
public class OnlineComputerImpl extends UnicastRemoteObject implements OnlineComputer {

    protected OnlineComputerImpl() throws RemoteException {
    }

    @Override
    public int add(int num1, int num2) throws RemoteException {
        return num1 + num2;
    }

    @Override
    public int subtract(int num1, int num2) throws RemoteException {
        return num1 - num2;
    }

    @Override
    public float divide(int num1, int num2) throws RemoteException {
        if(num2 == 0) {
            System.out.println("num2 can't be 0!");
            return 0;
        }
        return (float) num1 / num2;
    }

    @Override
    public int multiply(int num1, int num2) throws RemoteException {
        return num1 * num2;
    }
}
