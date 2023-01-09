package com.rany.cake.dingtalk.sdk.utils;

import com.rany.cake.dingtalk.sdk.properties.JedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class JedisUtil {

    private static JedisConfig jedisConfig;

    private static ShardedJedisPool shardedJedisPool;

    private static ReentrantLock INSTANCE_INIT_LOCL = new ReentrantLock(false);

    public static void init(JedisConfig jedisConfig) {
        JedisUtil.jedisConfig = jedisConfig;
        getInstance();
    }


    private static ShardedJedis getInstance() {
        if (shardedJedisPool == null) {
            try {
                if (INSTANCE_INIT_LOCL.tryLock(2, TimeUnit.SECONDS)) {

                    try {

                        if (shardedJedisPool == null) {

                            JedisPoolConfig config = new JedisPoolConfig();
                            config.setMaxTotal(jedisConfig.getMaxTotal());
                            config.setMaxIdle(jedisConfig.getMaxIdle());
                            config.setMinIdle(jedisConfig.getMinIdle());
                            config.setMaxWaitMillis(jedisConfig.getMaxWaitMillis());

                            List<JedisShardInfo> jedisShardInfos = new LinkedList<JedisShardInfo>();

                            String[] addressArr = jedisConfig.getAddress().split(",");
                            for (int i = 0; i < addressArr.length; i++) {
                                JedisShardInfo jedisShardInfo = new JedisShardInfo(addressArr[i]);
                                if (StringUtils.isNotBlank(jedisConfig.getPassword())) {
                                    jedisShardInfo.setPassword(jedisConfig.getPassword());
                                }
                                jedisShardInfos.add(jedisShardInfo);
                            }
                            shardedJedisPool = new ShardedJedisPool(config, jedisShardInfos);
                            log.info("JedisUtil.ShardedJedisPool init success.");
                        }

                    } finally {
                        INSTANCE_INIT_LOCL.unlock();
                    }
                }

            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (shardedJedisPool == null) {
            throw new NullPointerException("JedisUtil.ShardedJedisPool is null.");
        }

        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        return shardedJedis;
    }



    private static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }


    public static String setStringValue(String key, String value, int seconds) {
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key, seconds, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static String setObjectValue(String key, Object obj, int seconds) {
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key.getBytes(), seconds, serialize(obj));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static String getStringValue(String key) {
        String value = null;
        ShardedJedis client = getInstance();
        try {
            value = client.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return value;
    }

    public static Object getObjectValue(String key) {
        Object obj = null;
        ShardedJedis client = getInstance();
        try {
            byte[] bytes = client.get(key.getBytes());
            if (bytes != null && bytes.length > 0) {
                obj = unserialize(bytes);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return obj;
    }

    public static Long del(String key) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.del(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static Long incrBy(String key, int i) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.incrBy(key, i);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static boolean exists(String key) {
        Boolean result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.exists(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static long expire(String key, int seconds) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expire(key, seconds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }



    public static long expireAt(String key, long unixTime) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expireAt(key, unixTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }


}
