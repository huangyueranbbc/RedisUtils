package com.utils.redis.service;

import com.hyr.redis.RedisClusterProxy;
import com.hyr.redis.help.RedisHelper;
import com.thread.RedisThreadFactory;
import org.apache.log4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/*******************************************************************************
 * @date 2019-01-17 下午 2:33
 * @author: <a href=mailto:>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class RedisDeleteDataService {

    private final static Logger logger = Logger.getLogger(RedisDeleteDataService.class);

    private final static ThreadFactory threadFactory=new RedisThreadFactory("REDIS_THREAD_FACTORY");

    private static int coreNum=Runtime.getRuntime().availableProcessors();


    /**
     * get redis cluster instance, the instance is single
     *
     * @param hostAndPortAddress split by ','
     * @return
     */
    public static synchronized RedisClusterProxy getRedisCluster(String hostAndPortAddress) {
        Set<HostAndPort> hostAndPorts = RedisHelper.getHostAndPort(hostAndPortAddress);
        logger.info(hostAndPorts);
        return new RedisClusterProxy(hostAndPorts);
    }



    /**
     * command : 删除集群/单机指定格式的key redis默认匹配格式
     *
     * @param jedisCluster
     * @param pattern
     * @return
     */
    public static void deleteDatas(RedisClusterProxy jedisCluster, final String pattern) {
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            final CountDownLatch countDownLatch=new CountDownLatch(clusterNodes.size());
            final ThreadPoolExecutor executor=new ThreadPoolExecutor(coreNum*2,coreNum*2,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),threadFactory);
            for (String node : clusterNodes.keySet()) {
                JedisPool jedisPool = clusterNodes.get(node);
                if (jedisPool != null && !jedisPool.isClosed()) {
                    final Jedis jedis = jedisPool.getResource();
                        final Job job = new Job(new DeleteJobCall() {
                            @Override
                            public void call() {
                                try {
                                    final Set<String> keys = jedis.keys(pattern);
                                    for(String key : keys){
                                        jedis.del(key);
                                        logger.debug(MessageFormat.format("del key{0} success.",key));
                                    }
                                    String successMessage = MessageFormat.format("delete data from redis success. keys{0}", new Object[]{Arrays.toString(keys.toArray())});
                                    logger.info(successMessage);
                                }catch (Exception e){
                                    logger.error("delete keys has error.", e);
                                }finally {
                                    if(jedis!=null){
                                        jedis.close();
                                    }
                                    countDownLatch.countDown();
                                }
                            }
                        });
                        executor.submit(job);
                }
            }
            countDownLatch.await();
            executor.shutdown();
        } catch (Exception e) {
            logger.error("delete datas error.", e);
        }
    }

    /* ==================================================================================== */

    static class Job implements Runnable{

        Call call;

        Job(Call call) {
            this.call = call;
        }

        @Override
        public void run() {
            call.call();
        }
    }

    interface Call{
        void call();
    }

    interface DeleteJobCall extends Call{

    }

    /**
     * server main 服务启动入口
     * @param args
     */
    public static void main(String[] args) {
        String address=args[0];
        RedisClusterProxy jedisCluster = getRedisCluster(address);
        deleteDatas(jedisCluster,"*");
    }

}
