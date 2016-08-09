package com.qinjiangbo.zookeeper;

import java.util.Scanner;

/**
 * Created by Richard on 7/3/16.
 */
public class RMIServer {

    public static void main(String[] args) throws Exception{
        System.out.println("Please assign the host and port:");
        Scanner scanner = new Scanner(System.in);
        System.out.println("host:port");
        String url = scanner.nextLine();
        String[] params = url.split(":");
        if(params.length < 1) {
            throw new Exception("No enough params");
        }
        String host = params[0];
        int port = Integer.valueOf(params[1]).intValue();

        ServiceProvider serviceProvider = new ServiceProvider();
        OnlineComputer onlineComputer = new OnlineComputerImpl();
        serviceProvider.publish(onlineComputer, host, port);

        System.out.printf("Server at %s:%d started!", host, port);
        //Thread.sleep(Long.MAX_VALUE);
    }

}
