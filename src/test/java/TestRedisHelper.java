import com.hyr.redis.JedisMonitorProxy;
import com.hyr.redis.NodeSlots;
import com.hyr.redis.RedisClusterProxy;
import com.utils.redis.RedisClusterUtils;
import com.hyr.redis.message.ResultMessage;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.util.Slowlog;

import java.util.*;

/*******************************************************************************
 * @date 2018-02-28 下午 5:45
 * @author: <a href=mailto:>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class TestRedisHelper {

    private static Logger log = Logger.getLogger(TestRedisHelper.class);

    RedisClusterProxy jedisCluster = null;

    @Before
    public synchronized void before() {
        jedisCluster = RedisClusterUtils.getRedisClusterInstance("127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003,127.0.0.1:7004,127.0.0.1:7005,127.0.0.1:7006");
        //jedisCluster = RedisClusterUtils.getRedisClusterInstance("127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003");
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        Set<String> hostAndPorts = clusterNodes.keySet();
        log.info(hostAndPorts);
    }

    @After
    public void after() {
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }

    @Test
    public void testKeys() {
        TreeSet<String> keys = RedisClusterUtils.keys(jedisCluster, "*");
        System.out.println(keys);
    }

    @Test
    public void testInfos() {
        String result = RedisClusterUtils.info(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testInfo() {
        String result = RedisClusterUtils.info(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.server(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.clients(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.memory(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.persistence(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.state(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.cpu(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.cluster(jedisCluster);
        System.out.println(result);
        result = RedisClusterUtils.keyspace(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testInfoSection() {
        String section="memory"; // server clients memory persistence state cpu cluster keyspace
        String result = RedisClusterUtils.info(jedisCluster,section);
        System.out.println(result);
    }

    @Test
    public void testNodes() {
        String result = RedisClusterUtils.nodes(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testTransaction() {
        boolean result = RedisClusterUtils.transaction(jedisCluster, new RedisClusterUtils.RedisCallBack() {

            public List<String> setKey() {
                List<String> keys = new ArrayList<String>(); // set all keys to allot slot
                keys.add("a4");
                keys.add("a5");
                keys.add("a6");
                return keys;
            }

            public void OnMultiAndExecListener(Transaction transaction) {
                try {
                    transaction.set("a4", "b5");
                    transaction.set("a5", "b2");
                    transaction.set("a6", "b3");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        System.out.println(result);
    }

    @Test
    public void testCall() {
        String result = RedisClusterUtils.call(jedisCluster, "return redis.call('TIME')");
        System.out.println(result);
    }

    @Test
    public void testPing() {
        ResultMessage resultMessage = RedisClusterUtils.ping(jedisCluster);
        System.out.println(resultMessage.toString());
    }

    @Test
    public void testRandomKey() {
        String key = RedisClusterUtils.randomKey(jedisCluster);
        System.out.println(key);
    }

    @Test
    public void testIsKnownNode() {
        boolean result = RedisClusterUtils.isKnownNode(jedisCluster, "127.0.0.1", 7001);
        System.out.println(result);
        boolean result1 = RedisClusterUtils.isKnownNode(jedisCluster, "41baecb1e1b940a869e2a65bc202cbceaed38904");
        System.out.println(result1);
        boolean result2 = RedisClusterUtils.isKnownNode(jedisCluster, "41baecb1e1b9403a86qwfqwf8904");
        System.out.println(result2);
    }

    @Test
    public void testFlushDB() {
        boolean result = RedisClusterUtils.flushDB(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testSave() {
        boolean result = RedisClusterUtils.save(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testLastSave() {
        String result = RedisClusterUtils.lastSave(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testBgRewriteAof() {
        boolean result = RedisClusterUtils.bgRewriteAof(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testSlaveOfNoOne() {
        boolean result = RedisClusterUtils.slaveOfNoOne(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testBgSave() {
        boolean result = RedisClusterUtils.bgSave(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testDebug() {
        String result = RedisClusterUtils.debug(jedisCluster, "a1");
        System.out.println(result);
    }

    @Test
    public void testDelSlots() {
        ResultMessage resultMessage = RedisClusterUtils.delSlots(jedisCluster, 0, 65535);
        System.out.println(resultMessage);
    }

    @Test
    public void testAddSlots() {
        ResultMessage resultMessage = RedisClusterUtils.addSlots(jedisCluster, 0, 65535);
        System.out.println(resultMessage);
    }

    @Test
    public void testMeet() {
        ResultMessage result = RedisClusterUtils.meet(jedisCluster, "127.0.0.1", 7005);
        System.out.println(result);
    }

    @Test
    public void testForget() {
        ResultMessage result = RedisClusterUtils.forget(jedisCluster, "3af9bc9fff7c87f6b9b5753b504d55a5c2307383");
        System.out.println(result);
    }

    @Test
    public void testDbSize() {
        long result = RedisClusterUtils.dbSize(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testMoveSlot() {
        boolean result = RedisClusterUtils.moveSlot(jedisCluster, 7785, "127.0.0.1:7002");
        System.out.println(result);
    }

    @Test
    public void testClusterReplicate() {
        boolean result = RedisClusterUtils.replicate(jedisCluster, "127.0.0.1:7003", "127.0.0.1:7004");
        System.out.println(result);
    }

    @Test
    public void testClusterSlots() {
        List<NodeSlots> result = RedisClusterUtils.slots(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testSlotsStable() {
        boolean result = RedisClusterUtils.slotsStable(jedisCluster, 7785);
        System.out.println(result);
    }

    @Test
    public void testGetKeysInSlot() {
        List<String> keysInSlot = RedisClusterUtils.getKeysInSlot(jedisCluster, 7785, 100);
        System.out.println(keysInSlot);
    }

    @Test
    public void testDump() {
        byte[] result = RedisClusterUtils.dump(jedisCluster, "a1");
        assert result != null;
        System.out.println(new String(result));
    }

    @Test
    public void testRestore() {
        boolean result = RedisClusterUtils.restore(jedisCluster, "gg1", RedisClusterUtils.dump(jedisCluster, "a1"));
        System.out.println(result);
    }


    @Test
    public void testSlowlogGet() {
        List<Slowlog> result = RedisClusterUtils.slowlogGet(jedisCluster);
        assert result != null;
        for (Slowlog slowlog : result) {
            System.out.println(slowlog.getId() + "-" + slowlog.getTimeStamp() + "-" + slowlog.getExecutionTime() + "-" + slowlog.getArgs());
        }
    }

    @Test
    public void testSlowlogLen() {
        long result = RedisClusterUtils.slowlogLen(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testSunion() {
        Set<String> result = RedisClusterUtils.sunion(jedisCluster, "sb2", "sb3", "sb1", "sa1");
        System.out.println(result);
    }

    @Test
    public void testSunionstore() {
        boolean result = RedisClusterUtils.sunionstore(jedisCluster, "sunb1", "sb3", "sb1", "sb2");
        System.out.println(result);
    }

    @Test
    public void testMonitor() {
        JedisMonitor monitor = new JedisMonitor() {
            @Override
            public void onCommand(String s) {
                System.out.println(s);
            }
        };
        JedisMonitorProxy monitorProxy = new JedisMonitorProxy() {
            public void onCommand(String command, String hostAndPort) {
                System.out.println(hostAndPort + "--" + command);
            }

        };
        //RedisClusterUtils.monitor(jedisCluster,monitorProxy,1000000);
        RedisClusterUtils.monitor(jedisCluster, 1000000);
    }


}
