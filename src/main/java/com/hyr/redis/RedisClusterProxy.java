package com.hyr.redis;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.BinaryClient.LIST_POSITION;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*******************************************************************************
 * @date 2018-02-28 下午 5:45
 * @author: <a href=mailto:>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class RedisClusterProxy implements JedisCommands, BasicCommands, Closeable {

    public static final short HASHSLOTS = 16384;
    private static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_MAX_REDIRECTIONS = 5;
    private int maxRedirections;
    private JedisSlotBasedConnectionHandlerProxy connectionHandler;

    public RedisClusterProxy(Set<HostAndPort> nodes, int timeout) {
        this(nodes, timeout, 5);
    }

    public RedisClusterProxy(Set<HostAndPort> nodes) {
        this(nodes, 2000);
    }

    public RedisClusterProxy(Set<HostAndPort> nodes, int timeout, int maxRedirections) {
        this(nodes, timeout, maxRedirections, new GenericObjectPoolConfig());
    }

    public RedisClusterProxy(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig) {
        this(nodes, 2000, 5, poolConfig);
    }

    public RedisClusterProxy(Set<HostAndPort> nodes, int timeout, GenericObjectPoolConfig poolConfig) {
        this(nodes, timeout, 5, poolConfig);
    }

    public RedisClusterProxy(Set<HostAndPort> jedisClusterNode, int timeout, int maxRedirections, GenericObjectPoolConfig poolConfig) {
        this.connectionHandler = new JedisSlotBasedConnectionHandlerProxy(jedisClusterNode, poolConfig, timeout);
        this.maxRedirections = maxRedirections;
    }

    public void close() {
        if (this.connectionHandler != null) {
            Iterator i$ = this.connectionHandler.getNodes().values().iterator();

            while (i$.hasNext()) {
                JedisPool pool = (JedisPool) i$.next();

                try {
                    if (pool != null) {
                        pool.destroy();
                    }
                } catch (Exception var4) {
                    ;
                }
            }
        }

    }

    public String set(final String key, final String value) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.set(key, value);
            }
        }).run(key);
    }

    public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.set(key, value, nxxx, expx, time);
            }
        }).run(key);
    }

    public String get(final String key) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.get(key);
            }
        }).run(key);
    }

    public Boolean exists(final String key) {
        return (Boolean) (new JedisClusterCommand<Boolean>(this.connectionHandler, this.maxRedirections) {
            public Boolean execute(Jedis connection) {
                return connection.exists(key);
            }
        }).run(key);
    }

    public Long persist(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.persist(key);
            }
        }).run(key);
    }

    public String type(final String key) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.type(key);
            }
        }).run(key);
    }

    public Long expire(final String key, final int seconds) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.expire(key, seconds);
            }
        }).run(key);
    }

    public Long pexpire(final String key, final long milliseconds) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.pexpire(key, milliseconds);
            }
        }).run(key);
    }

    public Long expireAt(final String key, final long unixTime) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.expireAt(key, unixTime);
            }
        }).run(key);
    }

    public Long pexpireAt(final String key, final long millisecondsTimestamp) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.pexpireAt(key, millisecondsTimestamp);
            }
        }).run(key);
    }

    public Long ttl(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.ttl(key);
            }
        }).run(key);
    }

    public Boolean setbit(final String key, final long offset, final boolean value) {
        return (Boolean) (new JedisClusterCommand<Boolean>(this.connectionHandler, this.maxRedirections) {
            public Boolean execute(Jedis connection) {
                return connection.setbit(key, offset, value);
            }
        }).run(key);
    }

    public Boolean setbit(final String key, final long offset, final String value) {
        return (Boolean) (new JedisClusterCommand<Boolean>(this.connectionHandler, this.maxRedirections) {
            public Boolean execute(Jedis connection) {
                return connection.setbit(key, offset, value);
            }
        }).run(key);
    }

    public Boolean getbit(final String key, final long offset) {
        return (Boolean) (new JedisClusterCommand<Boolean>(this.connectionHandler, this.maxRedirections) {
            public Boolean execute(Jedis connection) {
                return connection.getbit(key, offset);
            }
        }).run(key);
    }

    public Long setrange(final String key, final long offset, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.setrange(key, offset, value);
            }
        }).run(key);
    }

    public String getrange(final String key, final long startOffset, final long endOffset) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.getrange(key, startOffset, endOffset);
            }
        }).run(key);
    }

    public String getSet(final String key, final String value) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.getSet(key, value);
            }
        }).run(key);
    }

    public Long setnx(final String key, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.setnx(key, value);
            }
        }).run(key);
    }

    public String setex(final String key, final int seconds, final String value) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.setex(key, seconds, value);
            }
        }).run(key);
    }

    public Long decrBy(final String key, final long integer) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.decrBy(key, integer);
            }
        }).run(key);
    }

    public Long decr(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.decr(key);
            }
        }).run(key);
    }

    public Long incrBy(final String key, final long integer) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.incrBy(key, integer);
            }
        }).run(key);
    }

    public Long incr(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.incr(key);
            }
        }).run(key);
    }

    public Long append(final String key, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.append(key, value);
            }
        }).run(key);
    }

    public String substr(final String key, final int start, final int end) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.substr(key, start, end);
            }
        }).run(key);
    }

    public Long hset(final String key, final String field, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.hset(key, field, value);
            }
        }).run(key);
    }

    public String hget(final String key, final String field) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.hget(key, field);
            }
        }).run(key);
    }

    public Long hsetnx(final String key, final String field, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.hsetnx(key, field, value);
            }
        }).run(key);
    }

    public String hmset(final String key, final Map<String, String> hash) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.hmset(key, hash);
            }
        }).run(key);
    }

    public List<String> hmget(final String key, final String... fields) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.hmget(key, fields);
            }
        }).run(key);
    }

    public Long hincrBy(final String key, final String field, final long value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.hincrBy(key, field, value);
            }
        }).run(key);
    }

    public Boolean hexists(final String key, final String field) {
        return (Boolean) (new JedisClusterCommand<Boolean>(this.connectionHandler, this.maxRedirections) {
            public Boolean execute(Jedis connection) {
                return connection.hexists(key, field);
            }
        }).run(key);
    }

    public Long hdel(final String key, final String... field) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.hdel(key, field);
            }
        }).run(key);
    }

    public Long hlen(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.hlen(key);
            }
        }).run(key);
    }

    public Set<String> hkeys(final String key) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.hkeys(key);
            }
        }).run(key);
    }

    public List<String> hvals(final String key) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.hvals(key);
            }
        }).run(key);
    }

    public Map<String, String> hgetAll(final String key) {
        return (Map) (new JedisClusterCommand<Map<String, String>>(this.connectionHandler, this.maxRedirections) {
            public Map<String, String> execute(Jedis connection) {
                return connection.hgetAll(key);
            }
        }).run(key);
    }

    public Long rpush(final String key, final String... string) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.rpush(key, string);
            }
        }).run(key);
    }

    public Long lpush(final String key, final String... string) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.lpush(key, string);
            }
        }).run(key);
    }

    public Long llen(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.llen(key);
            }
        }).run(key);
    }

    public List<String> lrange(final String key, final long start, final long end) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.lrange(key, start, end);
            }
        }).run(key);
    }

    public String ltrim(final String key, final long start, final long end) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.ltrim(key, start, end);
            }
        }).run(key);
    }

    public String lindex(final String key, final long index) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.lindex(key, index);
            }
        }).run(key);
    }

    public String lset(final String key, final long index, final String value) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.lset(key, index, value);
            }
        }).run(key);
    }

    public Long lrem(final String key, final long count, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.lrem(key, count, value);
            }
        }).run(key);
    }

    public String lpop(final String key) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.lpop(key);
            }
        }).run(key);
    }

    public String rpop(final String key) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.rpop(key);
            }
        }).run(key);
    }

    public Long sadd(final String key, final String... member) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.sadd(key, member);
            }
        }).run(key);
    }

    public Set<String> smembers(final String key) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.smembers(key);
            }
        }).run(key);
    }

    public Long srem(final String key, final String... member) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.srem(key, member);
            }
        }).run(key);
    }

    public String spop(final String key) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.spop(key);
            }
        }).run(key);
    }

    public Long scard(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.scard(key);
            }
        }).run(key);
    }

    public Boolean sismember(final String key, final String member) {
        return (Boolean) (new JedisClusterCommand<Boolean>(this.connectionHandler, this.maxRedirections) {
            public Boolean execute(Jedis connection) {
                return connection.sismember(key, member);
            }
        }).run(key);
    }

    public String srandmember(final String key) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.srandmember(key);
            }
        }).run(key);
    }

    public List<String> srandmember(final String key, final int count) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.srandmember(key, count);
            }
        }).run(key);
    }

    public Long strlen(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.strlen(key);
            }
        }).run(key);
    }

    public Long zadd(final String key, final double score, final String member) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zadd(key, score, member);
            }
        }).run(key);
    }

    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zadd(key, scoreMembers);
            }
        }).run(key);
    }

    public Set<String> zrange(final String key, final long start, final long end) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrange(key, start, end);
            }
        }).run(key);
    }

    public Long zrem(final String key, final String... member) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zrem(key, member);
            }
        }).run(key);
    }

    public Double zincrby(final String key, final double score, final String member) {
        return (Double) (new JedisClusterCommand<Double>(this.connectionHandler, this.maxRedirections) {
            public Double execute(Jedis connection) {
                return connection.zincrby(key, score, member);
            }
        }).run(key);
    }

    public Long zrank(final String key, final String member) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zrank(key, member);
            }
        }).run(key);
    }

    public Long zrevrank(final String key, final String member) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zrevrank(key, member);
            }
        }).run(key);
    }

    public Set<String> zrevrange(final String key, final long start, final long end) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrevrange(key, start, end);
            }
        }).run(key);
    }

    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrangeWithScores(key, start, end);
            }
        }).run(key);
    }

    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrevrangeWithScores(key, start, end);
            }
        }).run(key);
    }

    public Long zcard(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zcard(key);
            }
        }).run(key);
    }

    public Double zscore(final String key, final String member) {
        return (Double) (new JedisClusterCommand<Double>(this.connectionHandler, this.maxRedirections) {
            public Double execute(Jedis connection) {
                return connection.zscore(key, member);
            }
        }).run(key);
    }

    public List<String> sort(final String key) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.sort(key);
            }
        }).run(key);
    }

    public List<String> sort(final String key, final SortingParams sortingParameters) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.sort(key, sortingParameters);
            }
        }).run(key);
    }

    public Long zcount(final String key, final double min, final double max) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zcount(key, min, max);
            }
        }).run(key);
    }

    public Long zcount(final String key, final String min, final String max) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zcount(key, min, max);
            }
        }).run(key);
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrangeByScore(key, min, max);
            }
        }).run(key);
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrangeByScore(key, min, max);
            }
        }).run(key);
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrevrangeByScore(key, max, min);
            }
        }).run(key);
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrangeByScore(key, min, max, offset, count);
            }
        }).run(key);
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrevrangeByScore(key, max, min);
            }
        }).run(key);
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrangeByScore(key, min, max, offset, count);
            }
        }).run(key);
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrevrangeByScore(key, max, min, offset, count);
            }
        }).run(key);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrangeByScoreWithScores(key, min, max);
            }
        }).run(key);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrevrangeByScoreWithScores(key, max, min);
            }
        }).run(key);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrangeByScoreWithScores(key, min, max, offset, count);
            }
        }).run(key);
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrevrangeByScore(key, max, min, offset, count);
            }
        }).run(key);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrangeByScoreWithScores(key, min, max);
            }
        }).run(key);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrevrangeByScoreWithScores(key, max, min);
            }
        }).run(key);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrangeByScoreWithScores(key, min, max, offset, count);
            }
        }).run(key);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }
        }).run(key);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public Set<Tuple> execute(Jedis connection) {
                return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }
        }).run(key);
    }

    public Long zremrangeByRank(final String key, final long start, final long end) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zremrangeByRank(key, start, end);
            }
        }).run(key);
    }

    public Long zremrangeByScore(final String key, final double start, final double end) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zremrangeByScore(key, start, end);
            }
        }).run(key);
    }

    public Long zremrangeByScore(final String key, final String start, final String end) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zremrangeByScore(key, start, end);
            }
        }).run(key);
    }

    public Long zlexcount(final String key, final String min, final String max) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zlexcount(key, min, max);
            }
        }).run(key);
    }

    public Set<String> zrangeByLex(final String key, final String min, final String max) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrangeByLex(key, min, max);
            }
        }).run(key);
    }

    public Set<String> zrangeByLex(final String key, final String min, final String max, final int offset, final int count) {
        return (Set) (new JedisClusterCommand<Set<String>>(this.connectionHandler, this.maxRedirections) {
            public Set<String> execute(Jedis connection) {
                return connection.zrangeByLex(key, min, max, offset, count);
            }
        }).run(key);
    }

    public Long zremrangeByLex(final String key, final String min, final String max) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.zremrangeByLex(key, min, max);
            }
        }).run(key);
    }

    public Long linsert(final String key, final LIST_POSITION where, final String pivot, final String value) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.linsert(key, where, pivot, value);
            }
        }).run(key);
    }

    public Long lpushx(final String key, final String... string) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.lpushx(key, string);
            }
        }).run(key);
    }

    public Long rpushx(final String key, final String... string) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.rpushx(key, string);
            }
        }).run(key);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<String> blpop(final String arg) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.blpop(arg);
            }
        }).run(arg);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<String> brpop(final String arg) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.brpop(arg);
            }
        }).run(arg);
    }

    public Long del(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.del(key);
            }
        }).run(key);
    }

    public String echo(final String string) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.echo(string);
            }
        }).run((String) null);
    }

    public Long move(final String key, final int dbIndex) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.move(key, dbIndex);
            }
        }).run(key);
    }

    public Long bitcount(final String key) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.bitcount(key);
            }
        }).run(key);
    }

    public Long bitcount(final String key, final long start, final long end) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.bitcount(key, start, end);
            }
        }).run(key);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String ping() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.ping();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String quit() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.quit();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String flushDB() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.flushDB();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Long dbSize() {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.dbSize();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String select(final int index) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.select(index);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String flushAll() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.flushAll();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String auth(final String password) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.auth(password);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String save() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.save();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String bgsave() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.bgsave();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String bgrewriteaof() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.bgrewriteaof();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Long lastsave() {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.lastsave();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String shutdown() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.shutdown();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String info() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.info();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String info(final String section) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.info(section);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String slaveof(final String host, final int port) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.slaveof(host, port);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String slaveofNoOne() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.slaveofNoOne();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Long getDB() {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.getDB();
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String debug(final DebugParams params) {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.debug(params);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String configResetStat() {
        return (String) (new JedisClusterCommand<String>(this.connectionHandler, this.maxRedirections) {
            public String execute(Jedis connection) {
                return connection.configResetStat();
            }
        }).run((String) null);
    }

    public Map<String, JedisPool> getClusterNodes() {
        return this.connectionHandler.getNodes();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Long waitReplicas(int replicas, long timeout) {
        return null;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ScanResult<Entry<String, String>> hscan(final String key, final int cursor) {
        return (ScanResult) (new JedisClusterCommand<ScanResult<Entry<String, String>>>(this.connectionHandler, this.maxRedirections) {
            public ScanResult<Entry<String, String>> execute(Jedis connection) {
                return connection.hscan(key, cursor);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ScanResult<String> sscan(final String key, final int cursor) {
        return (ScanResult) (new JedisClusterCommand<ScanResult<String>>(this.connectionHandler, this.maxRedirections) {
            public ScanResult<String> execute(Jedis connection) {
                return connection.sscan(key, cursor);
            }
        }).run((String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ScanResult<Tuple> zscan(final String key, final int cursor) {
        return (ScanResult) (new JedisClusterCommand<ScanResult<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public ScanResult<Tuple> execute(Jedis connection) {
                return connection.zscan(key, cursor);
            }
        }).run((String) null);
    }

    public ScanResult<Entry<String, String>> hscan(final String key, final String cursor) {
        return (ScanResult) (new JedisClusterCommand<ScanResult<Entry<String, String>>>(this.connectionHandler, this.maxRedirections) {
            public ScanResult<Entry<String, String>> execute(Jedis connection) {
                return connection.hscan(key, cursor);
            }
        }).run(key);
    }

    public ScanResult<String> sscan(final String key, final String cursor) {
        return (ScanResult) (new JedisClusterCommand<ScanResult<String>>(this.connectionHandler, this.maxRedirections) {
            public ScanResult<String> execute(Jedis connection) {
                return connection.sscan(key, cursor);
            }
        }).run(key);
    }

    public ScanResult<Tuple> zscan(final String key, final String cursor) {
        return (ScanResult) (new JedisClusterCommand<ScanResult<Tuple>>(this.connectionHandler, this.maxRedirections) {
            public ScanResult<Tuple> execute(Jedis connection) {
                return connection.zscan(key, cursor);
            }
        }).run(key);
    }

    public Long pfadd(final String key, final String... elements) {
        return (Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.pfadd(key, elements);
            }
        }).run(key);
    }

    public long pfcount(final String key) {
        return ((Long) (new JedisClusterCommand<Long>(this.connectionHandler, this.maxRedirections) {
            public Long execute(Jedis connection) {
                return connection.pfcount(key);
            }
        }).run(key)).longValue();
    }

    public List<String> blpop(final int timeout, final String key) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.blpop(timeout, key);
            }
        }).run(key);
    }

    public List<String> brpop(final int timeout, final String key) {
        return (List) (new JedisClusterCommand<List<String>>(this.connectionHandler, this.maxRedirections) {
            public List<String> execute(Jedis connection) {
                return connection.brpop(timeout, key);
            }
        }).run(key);
    }

    public static enum Reset {
        SOFT,
        HARD;

        private Reset() {
        }
    }

    /**
     * get handler
     *
     * @return
     */
    public JedisSlotBasedConnectionHandlerProxy getConnectionHandler() {
        return connectionHandler;
    }
}
