package com.qinjiangbo.rmi;

import java.rmi.Naming;

/**
 * Created by Richard on 7/2/16.
 */
public class RMIClient {

    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost:1099/com.qinjiangbo.rmi.OnlineComputerImpl";
        OnlineComputer onlineComputer = (OnlineComputer) Naming.lookup(url);
        float factory = onlineComputer.divide(1, 7);
        System.out.println(factory);
    }

}
