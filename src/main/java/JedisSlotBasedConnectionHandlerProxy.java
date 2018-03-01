import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

import java.util.Set;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 *
 * @date 2018-03-01 上午 9:57 
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class JedisSlotBasedConnectionHandlerProxy extends JedisSlotBasedConnectionHandler{


    public JedisSlotBasedConnectionHandlerProxy(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig, int timeout) {
        super(nodes, poolConfig, timeout);
    }

}
