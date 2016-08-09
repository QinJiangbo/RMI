package com.qinjiangbo.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Richard on 7/2/16.
 */
public class RMIServer {

    public static void main(String[] args) throws Exception{
        int port = 1099;
        String url = "rmi://localhost:"+port+"/com.qinjiangbo.rmi.OnlineComputerImpl";
        LocateRegistry.createRegistry(port);
        Naming.rebind(url, new OnlineComputerImpl());
        System.out.println("RMI Server starts up!");
    }

}
