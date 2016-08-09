package com.qinjiangbo.zookeeper;

import org.slf4j.Logger;

/**
 * Created by Richard on 7/3/16.
 */
public class RMIClient {

    public static void main(String[] args) throws Exception{
        ServiceConsumer serviceConsumer = new ServiceConsumer();
        while (true) {
            OnlineComputer onlineComputer = serviceConsumer.lookUp();
            float result = onlineComputer.divide(1, 8);
            System.out.println(result);
            Thread.sleep(3000);
        }
    }

}
