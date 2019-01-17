package com.hyr.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

import java.util.Set;

/*******************************************************************************
 * @date 2018-03-01 上午 9:57
 * @author: <a href=mailto:>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class JedisSlotBasedConnectionHandlerProxy extends JedisSlotBasedConnectionHandler{


    public JedisSlotBasedConnectionHandlerProxy(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig, int timeout) {
        super(nodes, poolConfig, timeout);
    }

}
