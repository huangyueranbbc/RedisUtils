package com.hyr.redis;

import com.hyr.redis.help.RedisHelper;
import com.hyr.redis.message.ResultMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.text.SimpleDateFormat;
import java.util.*;

/*******************************************************************************
 * @date 2018-02-28 下午 5:45
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description: 集群环境redis操作指令
 ******************************************************************************/
public class RedisClusterUtils {

    private static Logger logger = Logger.getLogger(RedisClusterUtils.class);

    private static RedisClusterProxy jedisCluster = null;

    private RedisClusterUtils(RedisClusterProxy jedisCluster) {
        RedisClusterUtils.jedisCluster = jedisCluster;
    }

    /**
     * get redis cluster instance, the instance is single
     *
     * @param hostAndPortAddress split by ','
     * @return
     */
    public static synchronized RedisClusterProxy getRedisClusterInstance(String hostAndPortAddress) {
        if (jedisCluster == null) {
            if (StringUtils.isEmpty(hostAndPortAddress)) {
                return null;
            }
            Set<HostAndPort> hostAndPorts = getHostAndPort(hostAndPortAddress);
            jedisCluster = new RedisClusterProxy(hostAndPorts);
        }
        return jedisCluster;
    }

    /**
     * @param hostAndPortAddress
     * @return
     */
    private static synchronized Set<HostAndPort> getHostAndPort(String hostAndPortAddress) {
        Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
        String[] hostAndPortArray = hostAndPortAddress.split(",");
        for (String hostAndPort : hostAndPortArray) {
            String[] hostWithPort = hostAndPort.split(":");
            hostAndPorts.add(new HostAndPort(hostWithPort[0], Integer.parseInt(hostWithPort[1])));
        }
        return hostAndPorts;
    }


    /**
     * command : keys
     *
     * @param jedisCluster
     * @param pattern
     * @return
     */
    public static synchronized TreeSet<String> keys(RedisClusterProxy jedisCluster, String pattern) {
        TreeSet<String> keys = new TreeSet<String>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(node);
            if (jedisPool != null && !jedisPool.isClosed()) {
                Jedis jedis = jedisPool.getResource();
                try {
                    keys.addAll(jedis.keys(pattern));
                } catch (Exception e) {
                    logger.error("cluster keys is error. ", e);
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        }
        return keys;
    }

    /**
     * command : cluster info
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized String info(RedisClusterProxy jedisCluster) {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        StringBuilder sb = new StringBuilder();
        for (String node : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(node);
            if (jedisPool != null && !jedisPool.isClosed()) {
                Jedis jedis = jedisPool.getResource();
                try {
                    String info = jedis.info();
                    sb.append(info).append("=====================================================\n").append("\n");
                } catch (Exception e) {
                    logger.error("cluster info error!", e);
                } finally {
                    if (jedis != null) {
                        jedis.close();
                        logger.info("redis " + RedisHelper.getNodeId(jedis.clusterNodes()) + " is close.");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * command : cluster nodes
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized String nodes(RedisClusterProxy jedisCluster) {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        StringBuilder sb = new StringBuilder();
        for (String node : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(node);
            if (jedisPool != null && !jedisPool.isClosed()) {
                Jedis jedis = jedisPool.getResource();
                try {
                    String nodeInfo = jedis.clusterNodes();
                    for (String infoLine : nodeInfo.split("\n")) {
                        if (infoLine.contains("myself")) {
                            sb.append(infoLine.replace("myself,", "")).append("\n");
                        }
                    }
                } catch (Exception e) {
                    logger.error("cluster nodes is error.", e);
                } finally {
                    if (jedis != null) {
                        jedis.close();//用完一定要close这个链接！！！
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * command : call
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized String call(RedisClusterProxy jedisCluster, String script) {
        try {
            JedisSlotBasedConnectionHandlerProxy connectionHandler = jedisCluster.getConnectionHandler();
            Jedis redis = connectionHandler.getConnection();
            Object result = redis.eval(script);
            if (result != null && result instanceof List) {
                List list = (List) result;
                return (String) list.get(0);
            }
        } catch (Exception e) {
            logger.error("cluster call is error.", e);
        }
        return null;
    }

    /**
     * command : multi exec
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized boolean transaction(RedisClusterProxy jedisCluster, RedisCallBack redisCallBack) {
        boolean result;
        try {
            // TODO 改为可以识别散列方法
            // TODO 可以优化为动态代理
            result = redisCallBack.MultiAndExec(jedisCluster);

        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    abstract public static class RedisCallBack {

        boolean MultiAndExec(RedisClusterProxy jedisCluster) {
            JedisSlotBasedConnectionHandlerProxy connectionHandler = jedisCluster.getConnectionHandler();
            Jedis m$ = getMasterNode(jedisCluster);
            if (null == m$ || !m$.isConnected()) {
                return false;
            }
            boolean result = true;
            try {
                List<String> keys = setKey();
                allotSlot(m$, jedisCluster, keys, connectionHandler);
                // redis.asking(); // if has used clusterImporting or clusterMigrating to change slots, you need asking befor operation in client.
                Transaction transaction = m$.multi();
                OnMultiAndExecListener(transaction);
                transaction.exec();
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
            return result;
        }

        /**
         * get a master node
         *
         * @param jedisCluster
         * @return
         */
        private Jedis getMasterNode(RedisClusterProxy jedisCluster) {
            Map<String, JedisPool> jps$ = jedisCluster.getClusterNodes();
            for (JedisPool i$ : jps$.values()) {
                Jedis redis = i$.getResource();
                String nodesInfo = redis.info();
                if (nodesInfo.contains("role:master")) {
                    return redis;
                }
            }
            return null;
        }

        /**
         * allot slot by key
         *
         * @param t$
         * @param jedisCluster
         * @param keys
         * @param connectionHandler
         */
        void allotSlot(Jedis t$, RedisClusterProxy jedisCluster, List<String> keys, JedisSlotBasedConnectionHandlerProxy connectionHandler) {
            try {
                for (String key : keys) {
                    int slot = JedisClusterCRC16.getSlot(key);

                    Map<String, JedisPool> jps$ = jedisCluster.getClusterNodes();
                    for (JedisPool jedisPool : jps$.values()) {
                        Jedis redis = jedisPool.getResource();
                        String result = redis.clusterSetSlotNode(slot, RedisHelper.getNodeId(t$.clusterNodes()));
                        logger.info("ask node:" + RedisHelper.getNodeId(redis.clusterNodes()) + " , slot:" + slot + " to node:" + RedisHelper.getNodeId(t$.clusterNodes()) + " is " + result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * set all keys to allot slot
         *
         * @return keys
         */
        public abstract List<String> setKey();

        public abstract void OnMultiAndExecListener(Transaction transaction);
    }

    /**
     * command : ping
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized ResultMessage ping(RedisClusterProxy jedisCluster) {
        ResultMessage resultMessage = ResultMessage.buildOK();
        boolean result = true;
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        List<String> errorInfo = new ArrayList<String>();
        for (String node : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(node);
            if (jedisPool != null && !jedisPool.isClosed()) {
                Jedis jedis = jedisPool.getResource();
                try {
                    if (!jedis.ping().equals("PONG")) {
                        result = false;
                        errorInfo.add(RedisHelper.getNodeId(jedis.clusterNodes()) + "is failed!");
                    }
                } catch (Exception e) {
                    resultMessage.setResult(false);
                    resultMessage.setInfos(e.getMessage());
                    logger.error("cluster ping is error.", e);
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        }
        resultMessage.setResult(result);
        if (!result) {
            resultMessage.setInfos(errorInfo);
        }
        return resultMessage;
    }

    /**
     * command : random key
     *
     * @param jedisCluster
     * @return key
     */
    public static synchronized String randomKey(RedisClusterProxy jedisCluster) {
        JedisPool jedisPool = null;
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            Object[] hostAndPorts = clusterNodes.keySet().toArray();
            int randNum = new Random().nextInt(clusterNodes.size() - 1);
            int maxCount = clusterNodes.size() * 6;
            int index = 0;
            jedisPool = clusterNodes.get(hostAndPorts[randNum]);
            Set<String> keys = jedisPool.getResource().keys("*");
            while (!jedisPool.isClosed() && keys.size() == 0 && index < maxCount) {
                index++;
                randNum = new Random().nextInt(clusterNodes.size() - 1);
                keys = jedisPool.getResource().keys("*");
                jedisPool = clusterNodes.get(hostAndPorts[randNum]);
            }
            return jedisPool.getResource().randomKey();
        } catch (Exception e) {
            logger.error("cluster randomKey is error. ", e);
        }
        return null;
    }

    /**
     * test known node
     *
     * @param redisClusterProxy
     * @param nodeId
     * @return result
     */
    public static synchronized boolean isKnownNode(RedisClusterProxy redisClusterProxy, String nodeId) {
        boolean result = false;
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();

        for (String node : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(node);
            if (jedisPool != null && !jedisPool.isClosed()) {
                String nodesInfo = jedisPool.getResource().clusterNodes();
                for (String infoLine : nodesInfo.split("\n")) {
                    for (String info : infoLine.split(" ")) {
                        if (info.equals(nodeId)) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * test known node
     *
     * @param jedisCluster
     * @param host
     * @param port
     * @return
     */
    public static synchronized boolean isKnownNode(RedisClusterProxy jedisCluster, String host, int port) {
        Jedis redis = new Jedis(host, port);
        String nodeId = RedisHelper.getNodeId(redis.clusterNodes());
        return isKnownNode(jedisCluster, nodeId);
    }

    /**
     * command : flushdb
     *
     * @param redisClusterProxy
     * @return
     */
    public static synchronized boolean flushDB(RedisClusterProxy redisClusterProxy) {
        boolean result = true;
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            Jedis redis = null;
            try {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    redis = jedisPool.getResource();
                    String nodesInfo = redis.info();
                    if (nodesInfo.contains("role:master")) {
                        redis.flushAll();
                    }
                }
            } catch (Exception e) {
                result = false;
                logger.error("cluster flushDB is error.", e);
            } finally {
                if (redis != null) {
                    redis.close();
                }
            }
        }
        return result;
    }

    /**
     * command : save
     *
     * @param redisClusterProxy
     * @return
     */
    public static synchronized boolean save(RedisClusterProxy redisClusterProxy) {
        boolean result = true;
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            Jedis redis = null;
            try {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    redis = jedisPool.getResource();
                    redis.save();
                }
            } catch (Exception e) {
                logger.error("cluster save is error.", e);
                result = false;
            } finally {
                if (redis != null) {
                    redis.close();
                }
            }
        }
        return result;
    }

    /**
     * command : last save
     *
     * @param redisClusterProxy
     * @return
     */
    public static synchronized String lastSave(RedisClusterProxy redisClusterProxy) {
        StringBuilder sb = new StringBuilder();
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            Jedis redis = null;
            try {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    redis = jedisPool.getResource();
                    Long lastSaveTime = redis.lastsave();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(new Date(lastSaveTime));
                    for (String infoLine : redis.clusterNodes().split("\n")) {
                        if (infoLine.contains("myself")) {
                            String[] infos = infoLine.split(" ");
                            sb.append(infos[0]).append("\t").append(infos[1]).append("\t").append(time).append("\n");
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("cluster lastSave is error.", e);
            } finally {
                if (redis != null) {
                    redis.close();
                }
            }

        }
        return sb.toString();
    }

    /**
     * command : background rewrite aof file
     *
     * @param redisClusterProxy
     * @return
     */
    public static synchronized boolean bgRewriteAof(RedisClusterProxy redisClusterProxy) {
        boolean result = true;
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            Jedis redis = null;
            try {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    redis = jedisPool.getResource();
                    redis.bgrewriteaof();
                }
            } catch (Exception e) {
                result = false;
                logger.error("cluster bgRewriteAof is error.", e);
            } finally {
                if (redis != null) {
                    redis.close();
                }
            }
        }
        return result;
    }

    /**
     * canceling the primary server Association, close slave copy. it not allowed in cluster .
     *
     * @param redisClusterProxy
     * @return
     */
    @Deprecated
    public static synchronized boolean slaveOfNoOne(RedisClusterProxy redisClusterProxy) {
        boolean result = true;
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            Jedis redis = null;
            try {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    redis = jedisPool.getResource();
                    redis.slaveofNoOne();
                }
            } catch (Exception e) {
                result = false;
                logger.error("cluster slaveOfNoOne is error.", e);
            } finally {
                if (redis != null) {
                    redis.close();
                }
            }
        }
        return result;
    }

    /**
     * command : background save
     *
     * @param redisClusterProxy
     * @return
     */
    public static synchronized boolean bgSave(RedisClusterProxy redisClusterProxy) {
        boolean result = true;
        Map<String, JedisPool> clusterNodes = redisClusterProxy.getClusterNodes();
        for (String node : clusterNodes.keySet()) {
            Jedis redis = null;
            try {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    redis = jedisPool.getResource();
                    redis.bgsave();
                }
            } catch (Exception e) {
                result = false;
                logger.error("cluster bgSave is error.", e);
            } finally {
                if (redis != null) {
                    redis.close();
                }
            }
        }
        return result;
    }

    /**
     * command : debug
     *
     * @param redisClusterProxy
     * @param pattern
     * @return
     */
    public static synchronized String debug(RedisClusterProxy redisClusterProxy, String pattern) {
        String result;
        Jedis redis = null;
        try {
            JedisSlotBasedConnectionHandlerProxy connectionHandler = redisClusterProxy.getConnectionHandler();
            int slot = JedisClusterCRC16.getSlot(pattern);
            redis = connectionHandler.getConnectionFromSlot(slot);
            result = redis.debug(DebugParams.OBJECT(pattern));
        } catch (Exception e) {
            result = "debug fail ! " + e.getMessage();
            logger.error("cluster debug is error.", e);
        } finally {
            if (redis != null) {
                redis.close();
            }
        }
        return result;
    }

    /**
     * command : delslots
     *
     * @param redisClusterProxy
     * @param slots
     * @return
     */
    public static synchronized ResultMessage delSlots(RedisClusterProxy redisClusterProxy, Integer... slots) {
        ResultMessage result = ResultMessage.buildOK();
        Jedis target = null;
        JedisSlotBasedConnectionHandlerProxy connectionHandler = redisClusterProxy.getConnectionHandler();
        if (slots == null || slots.length < 1) {
            return ResultMessage.build(false, "slots is null");
        }
        List<String> errorInfo = new ArrayList<String>();
        for (Integer slot : slots) {
            try {
                target = connectionHandler.getConnectionFromSlot(slot);
                target.clusterDelSlots(slot);
            } catch (Exception e) {
                errorInfo.add(slot + " is failed !\t" + e.getMessage());
                logger.error("cluster delSlots is error.", e);
            } finally {
                if (target != null && target.isConnected()) {
                    target.close();
                }
            }
        }
        if (errorInfo.size() > 0) {
            result.setResult(false);
            result.setInfos(errorInfo);
        }
        return result;
    }

    /**
     * command : addSlots
     *
     * @param redisClusterProxy
     * @param slots
     * @return
     */
    public static synchronized ResultMessage addSlots(RedisClusterProxy redisClusterProxy, Integer... slots) {
        ResultMessage result = ResultMessage.buildOK();
        Jedis target = null;
        JedisSlotBasedConnectionHandlerProxy connectionHandler = redisClusterProxy.getConnectionHandler();
        if (slots == null || slots.length < 1) {
            return ResultMessage.build(false, "slots is null");
        }
        List<String> errorInfo = new ArrayList<String>();
        for (Integer slot : slots) {
            try {
                target = connectionHandler.getConnectionFromSlot(slot);
                target.clusterAddSlots(slot);
            } catch (Exception e) {
                errorInfo.add(slot + " is failed !\t" + e.getMessage());
                logger.error("cluster addSlots is error.", e);
            } finally {
                if (target != null && target.isConnected()) {
                    target.close();
                }
            }
        }
        if (errorInfo.size() > 0) {
            result.setResult(false);
            result.setInfos(errorInfo);
        }
        return result;
    }

    /**
     * command : meet
     *
     * @param redisClusterProxy
     * @param host
     * @param port
     * @return
     */
    public static synchronized ResultMessage meet(RedisClusterProxy redisClusterProxy, String host, Integer port) {
        ResultMessage resultMessage = ResultMessage.buildOK();
        Jedis target = null;
        try {
            JedisSlotBasedConnectionHandlerProxy connectionHandler = redisClusterProxy.getConnectionHandler();
            Map<String, JedisPool> jedisPools = connectionHandler.getNodes();
            Iterator<JedisPool> i$ = jedisPools.values().iterator();
            for (; i$.hasNext(); ) {
                JedisPool jedisPool = i$.next();
                target = jedisPool.getResource();
                target.clusterMeet(host, port);
                target.close();
            }
        } catch (Exception e) {
            if (target != null) {
                target.close();
            }
            resultMessage.setResult(false);
            resultMessage.setInfos(e.getMessage());
            logger.error("cluster meet is error.", e);
        }
        return resultMessage;
    }

    /**
     * command : forget
     *
     * @param redisClusterProxy
     * @param nodeId
     * @return
     */
    public static synchronized ResultMessage forget(RedisClusterProxy redisClusterProxy, String nodeId) {
        ResultMessage resultMessage = ResultMessage.buildOK();
        Jedis target = null;
        try {
            JedisSlotBasedConnectionHandlerProxy connectionHandler = redisClusterProxy.getConnectionHandler();
            Map<String, JedisPool> jedisPools = connectionHandler.getNodes();
            Iterator<JedisPool> i$ = jedisPools.values().iterator();
            for (; i$.hasNext(); ) {
                JedisPool jedisPool = i$.next();
                if (!RedisHelper.getNodeId(jedisPool.getResource().clusterNodes()).equals(nodeId)) { // not del selt
                    target = jedisPool.getResource();
                    target.clusterForget(nodeId);
                }
            }
        } catch (Exception e) {
            resultMessage.setResult(false);
            resultMessage.setInfos(e.getMessage());
            logger.error("cluster forget is error.", e);
        } finally {
            if (target != null) {
                target.close();
            }
        }
        return resultMessage;
    }

    /**
     * command : dbSize
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized long dbSize(RedisClusterProxy jedisCluster) {
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            long dbSize = 0;
            for (String node : clusterNodes.keySet()) {
                JedisPool i$ = clusterNodes.get(node);
                if (i$ == null || i$.isClosed()) {
                    logger.warn("Pool is null or Could not get a resource from the pool !");
                    return -1;
                }
                Jedis redis = i$.getResource();
                String nodesInfo = redis.info();
                if (nodesInfo.contains("role:master")) {
                    dbSize += redis.dbSize();
                }
            }
            return dbSize;
        } catch (Exception e) {
            logger.error("cluster dbSize error. ", e);
        }
        return -1;
    }

    /**
     * command : importing migrating
     *
     * @param jedisCluster
     * @param slot
     * @param sourceAddress ip:port
     * @param targetAddress ip:port
     * @return
     */
    public static synchronized boolean moveSlot(RedisClusterProxy jedisCluster, int slot, String sourceAddress, String targetAddress) {
        boolean result = true;
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            JedisPool s$ = clusterNodes.get(sourceAddress);
            JedisPool t$ = clusterNodes.get(targetAddress);
            if (s$ == null || s$.isClosed() || t$ == null || t$.isClosed()) {
                logger.warn(s$ + " is cloesd ! or" + t$ + " is closed !");
                return false;
            }
            String r1$ = t$.getResource().clusterSetSlotImporting(slot, RedisHelper.getNodeId(s$.getResource().clusterNodes()));
            logger.info("importing is " + r1$ + "!");
            String r2$ = s$.getResource().clusterSetSlotMigrating(slot, RedisHelper.getNodeId(t$.getResource().clusterNodes()));
            logger.info("migrating is " + r2$ + "!");
        } catch (Exception e) {
            result = false;
            logger.error("cluster moveSlot is error.", e);
        }
        return result;
    }

    /**
     * command : clusterReplicate
     *
     * @param jedisCluster
     * @param sourceAddress
     * @param targetAddress
     * @return
     */
    public static synchronized boolean replicate(RedisClusterProxy jedisCluster, String sourceAddress, String targetAddress) {
        boolean result = true;
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            JedisPool s$ = clusterNodes.get(sourceAddress);
            JedisPool t$ = clusterNodes.get(targetAddress);
            if (s$ == null || s$.isClosed() || t$ == null || t$.isClosed()) {
                logger.warn(s$ + " is cloesd ! or" + t$ + " is closed !");
                return false;
            }
            t$.getResource().clusterReplicate(RedisHelper.getNodeId(s$.getResource().clusterNodes()));
        } catch (Exception e) {
            result = false;
            logger.error("cluster replicate is error.", e);
        }
        return result;
    }

    /**
     * command : slots
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized List<NodeSlots> slots(RedisClusterProxy jedisCluster) {
        List<NodeSlots> nodeSlots = new ArrayList<NodeSlots>();
        try {
            JedisSlotBasedConnectionHandlerProxy connectionHandler = jedisCluster.getConnectionHandler();
            Jedis j$ = connectionHandler.getConnection();
            List<Object> clusterSlots = j$.clusterSlots();
            for (Object clusterSlot : clusterSlots) {
                List<Object> list = (List<Object>) clusterSlot;
                List<Object> master = (List<Object>) list.get(2);
                nodeSlots.add(new NodeSlots((Long) list.get(0), (Long) list.get(1), new String((byte[]) master.get(0)), (Long) master.get(1)));
            }
        } catch (Exception e) {
            logger.error("cluster slots is error.", e);
            return null;
        }
        return nodeSlots;
    }

    /**
     * command : setslot stable
     *
     * @param jedisCluster
     * @return
     */
    public static synchronized boolean slotsStable(RedisClusterProxy jedisCluster, Integer... slots) {
        boolean result = true;
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            for (String node : clusterNodes.keySet()) {
                JedisPool i$ = clusterNodes.get(node);
                Jedis redis = i$.getResource();
                for (Integer slot : slots) {
                    redis.clusterSetSlotStable(slot);
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error("cluster setslots stable is error.",e);
        }

        return result;
    }

}

