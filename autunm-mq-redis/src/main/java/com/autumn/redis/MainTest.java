package com.autumn.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
import redis.rmq.Callback;
import redis.rmq.Consumer;
import redis.rmq.Producer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: autumn
 * @description:
 * @author: qiushi
 * @create: 2021-04-15:14:33
 */
public class MainTest {



  public static void main(String[] args) throws URISyntaxException, InterruptedException {

    URI uri = new URI("redis://:UkpMEznstss34@172.16.0.47:6380/6?timeout=5000ms");
    Producer p = new Producer(new Jedis(uri), "some cool topic");
    p.publish("some cool message");

    for (int i = 0; i < 10; i++) {
      Thread.sleep(1000);

      p.publish(i+"");
      System.out.println(i);
    }

//    Consumer c = new Consumer(new Jedis(uri), "consumer identifier", "some cool topic");
//    c.consume(
//        new Callback() {
//          @Override
//          public void onMessage(String message) {
//            System.out.println("c:"+message);
//          }
//        });

    p.publish("some cool message2");
    Consumer c2 = new Consumer(new Jedis(uri), "consumer identifier", "some cool topic");
    String message = c2.consume();

    System.out.println("c2:"+message);

    p.publish("some cool message3");
    Consumer c3 = new Consumer(new Jedis(uri), "consumer identifier", "some cool topic");
    message = c3.read();
    System.out.println("c3:"+message);
  }
}
