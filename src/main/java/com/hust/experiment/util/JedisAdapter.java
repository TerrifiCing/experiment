package com.hust.experiment.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisAdapter implements InitializingBean {
    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        //jedis = new Jedis("localhost");
        pool = new JedisPool("localhost", 6379);
    }

    public Jedis getjedis(){
        return pool.getResource();
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void set(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.set(key,value);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }
}
