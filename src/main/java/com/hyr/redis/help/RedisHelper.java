package com.hyr.redis.help;

import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

/*******************************************************************************
 * @date 2018-03-01 下午 2:11
 * @author: <a href=mailto:>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class RedisHelper {

    /**
     * get node id string
     *
     * @param nodeInfos
     * @return
     */
    public static String getNodeId(String nodeInfos) {
        for (String infoLine : nodeInfos.split("\n")) {
            if (infoLine.contains("myself")) {
                return infoLine.split(" ")[0];
            }
        }
        return "";
    }

    /**
     * @param hostAndPortAddress
     * @return
     */
    public static synchronized Set<HostAndPort> getHostAndPort(String hostAndPortAddress) {
        Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
        String[] hostAndPortArray = hostAndPortAddress.split(",");
        for (String hostAndPort : hostAndPortArray) {
            String[] hostWithPort = hostAndPort.split(":");
            hostAndPorts.add(new HostAndPort(hostWithPort[0], Integer.parseInt(hostWithPort[1])));
        }
        return hostAndPorts;
    }

}
