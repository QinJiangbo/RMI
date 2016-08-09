package com.qinjiangbo.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.ConnectException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Richard on 7/3/16.
 */
public class ServiceConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConsumer.class);

    // 用于等待 SyncConnected 事件触发后继续执行当前线程
    private CountDownLatch latch = new CountDownLatch(1);

    // 定义一个 volatile 成员变量，用于保存最新的 RMI 地址
    //（考虑到该变量或许会被其它线程所修改，一旦修改后，该变量的值会影响到所有线程）
    private volatile List<String> urlList = new ArrayList<String>();

    // 构造器
    public ServiceConsumer() {
        ZooKeeper zooKeeper = connectServer();
        if(zooKeeper != null) {
            watchNode(zooKeeper);
        }
    }

    // 查找 RMI 服务
    public <T extends Remote> T lookUp() {
        T service = null;
        int size = urlList.size();
        if(size > 0) {
            String url;
            if(size == 1) {
                url = urlList.get(0); // 若 urlList 中只有一个元素，则直接获取该元素
                logger.debug("using only url {}", url);
            } else {
                url = urlList.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("using random url {}", url);
            }
            service = lookUpService(url);
        }
        return service;
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

    // 观察 /registry 节点下所有子节点是否有变化
    private void watchNode(final ZooKeeper zooKeeper) {
        try {
            List<String> nodeList = zooKeeper.getChildren(ZKParams.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(zooKeeper); // 若子节点有变化，则重新调用该方法（为了获取最新子节点中的数据
                    }
                }
            });
            // 用于存放 /registry 所有子节点中的数据
            List<String> dataList = new ArrayList<String>();
            for(String node : nodeList) {
                // 获取 /registry 的子节点中的数据
                byte[] data = zooKeeper.getData(ZKParams.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(data));
            }
            logger.debug("node data: {}", dataList);
            urlList = dataList; // 更新最新的 RMI 地址
        }catch (KeeperException | InterruptedException e) {
            logger.error("", e);
        }
    }

    // 在 JNDI 中查找 RMI 远程服务对象
    private <T> T lookUpService(String url) {
        T remote = null;
        try {
            remote = (T) Naming.lookup(url);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            if(e instanceof ConnectException) {
                // 若连接中断，则使用 urlList 中第一个 RMI 地址来查找（这是一种简单的重试方式，确保不会抛出异常）
                logger.error("ConnectException -> url: {}", url);
                if(urlList.size() != 0) {
                    url = urlList.get(0);
                    return lookUpService(url);
                }
            }
            logger.error("", e);
        }
        return remote;
    }
}
