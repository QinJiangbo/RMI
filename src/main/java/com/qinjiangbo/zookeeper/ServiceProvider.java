package com.qinjiangbo.zookeeper;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;

/**
 * RMI服务提供者
 * Created by Richard on 7/2/16.
 */
public class ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

    // 用于等待 SyncConnected 事件触发后继续执行当前线程
    private CountDownLatch latch = new CountDownLatch(1);

    // 发布 RMI 服务并注册 RMI 地址到 ZooKeeper 中
    public void publish(Remote remote, String host, int port) {
        String url = publishService(remote, port, host);
        logger.debug("service {} published!", url);
        if(url != null) {
            ZooKeeper zooKeeper = connectServer();
            if(zooKeeper != null) {
                createNode(zooKeeper, url);
            }
        }
    }

    // 发布 RMI 服务
    public String publishService(Remote remote, int port, String host) {
        String url = null;
        try {
            url = String.format("rmi://%s:%d/%s", host, port, remote.getClass().getName());
            LocateRegistry.createRegistry(port);
            Naming.rebind(url, remote);
            logger.debug("publish rmi service (url: {})", url);
        } catch (RemoteException | MalformedURLException e) {
            logger.error("", e);
        }
        return url;
    }

    // 连接 ZooKeeper 服务器
    private ZooKeeper connectServer() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(ZKParams.ZK_CONNECTION_STRING, ZKParams.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        }catch (IOException | InterruptedException e) {
            logger.error("", e);
        }
        return zooKeeper;
    }

    private void createNode(ZooKeeper zooKeeper, String url) {
        try {
            byte[] data = url.getBytes();
            // 创建一个临时性且有序的 ZNode
            String path = zooKeeper.create(ZKParams.ZK_PROVIDER_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create zookeeper node ({} => {})", path, url);
        }catch (KeeperException | InterruptedException e) {
            logger.error("", e);
        }
    }
}

