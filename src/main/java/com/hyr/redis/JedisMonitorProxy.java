package com.hyr.redis;

/*******************************************************************************
 * @date 2018-03-20 上午 11:35
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description: monitor代理
 ******************************************************************************/

import redis.clients.jedis.Client;
import redis.clients.jedis.JedisMonitor;

public abstract class JedisMonitorProxy extends JedisMonitor {
    protected Client client;

    public JedisMonitorProxy() {
    }

    public void proceed(Client client) {
        this.client = client;
        this.client.setTimeoutInfinite();

        do {
            String command = client.getBulkReply();
            String hostAndPort = client.getHost() + ":" + client.getPort();
            this.onCommand(command, hostAndPort);
        } while (client.isConnected());

    }

    public void onCommand(String s) {

    }

    public abstract void onCommand(String command, String hostAndPort);
}

