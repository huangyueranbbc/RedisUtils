import com.hyr.redis.help.RedisHelper;
import com.hyr.redis.message.ResultMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

import static org.junit.Assert.assertEquals;

/*******************************************************************************
 * @date 2018-02-28 下午 5:45
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class TestRedisHelper {

    RedisClusterProxy jedisCluster;

    @Before
    public void before() {
        jedisCluster = RedisClusterUtils.getRedisClusterInstance("127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003,127.0.0.1:7004,127.0.0.1:7005,127.0.0.1:7006");
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        Set<String> hostAndPorts = clusterNodes.keySet();
        System.out.println(hostAndPorts);
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
    public void testInfo() {
        String result = RedisClusterUtils.info(jedisCluster);
        System.out.println(result);
    }

    @Test
    public void testNodes() {
        String result = RedisClusterUtils.nodes(jedisCluster);
        System.out.println(result);
    }

//    @Test
//    public void testTransaction() {
//        boolean result = RedisClusterUtils.transaction(jedisCluster, new RedisClusterUtils.RedisCallBack() {
//
//            protected List<String> setKey() {
//                List<String> keys = new ArrayList<String>(); // set all keys to allot slot
//                keys.add("a4");
//                keys.add("a5");
//                keys.add("a6");
//                return keys;
//            }
//
//            void OnMultiAndExecListener(Transaction transaction) {
//                try {
////                    transaction.set("a1", "b5");
////                    transaction.set("a2", "b2");
////                    transaction.set("a3", "b3");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        System.out.println(result);
//
//    }

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


}
