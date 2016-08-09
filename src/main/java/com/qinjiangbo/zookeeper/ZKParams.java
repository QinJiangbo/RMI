package com.qinjiangbo.zookeeper;

/**
 * Created by Richard on 7/3/16.
 */
public class ZKParams {

    public static String ZK_CONNECTION_STRING = "localhost:2181";
    public static int ZK_SESSION_TIMEOUT = 5000;
    public static String ZK_REGISTRY_PATH = "/registry";
    public static String ZK_PROVIDER_PATH = ZK_REGISTRY_PATH + "/provider";

}
