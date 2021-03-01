package com.autumn.data.redis.service;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 *
 * @author: qiushi
 * @since: 2019-01-09 15:18
 */
@Slf4j
@Service
public class RedisLockService<T> {

    /**
     * 获取锁最大等待时间
     */
    private static long           maxWaitTime = 15 * 1000;
    @Resource
    private        RedissonClient redissonClient;

    /**
     * 使用分布式锁执行，并返回执行结果
     *
     * @param lockName       锁名
     * @param lockTimeMillis 上/执锁时间
     * @param runnable       得到锁后执行接口
     * @return T
     */
    public T lockAndRun(String lockName, Long lockTimeMillis, Runnable runnable) throws Exception {

        T       obj;
        RLock   rLock  = null;
        boolean isLock = false;

        try {
            rLock = redissonClient.getLock(lockName);

            if (rLock == null) {
                log.error("RedisLock--->rLock is null");
                throw new RuntimeException("RedisLock--->lock error, can not get lock resource !!!");
            }

            if (lockTimeMillis == null || lockTimeMillis.equals(0L)) {
                lockTimeMillis = maxWaitTime;
            }

            isLock = this.lock(rLock, lockTimeMillis);
            if (isLock) {
                obj = (T) runnable.run();
            } else {
                throw new RuntimeException("RedisLock--->lock error , lock name [" + lockName + "] is not Locked!!!");
            }
        } finally {
            if (isLock) {
                this.unlock(rLock);
            }
        }
        return obj;

    }

    /**
     * 使用分布式锁执行，并返回执行结果
     *
     * @param runnable 分布式锁执行对象
     * @return 执行结果
     * @throws Exception 异常
     */

    public T lockAndRun(RedisLockRunnable runnable) throws Exception {
        return this.lockAndRun(runnable.getLockKey(), runnable.getLockTime(), runnable::run);
    }

    /**
     * @return 是否获得锁成功
     */
    private boolean lock(RLock rLock, long lockWaitTime) {
        try {
            return rLock.tryLock(lockWaitTime, lockWaitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // 中断异常
            log.warn("RedisLock--->lock error, exception info : is not Locked!!!", e);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param rLock 锁对象
     */
    private void unlock(RLock rLock) {
        try {
            if (rLock != null) {
                rLock.unlock();
            }
        } catch (Exception e) {
            log.error("RedisLock--->unlock error, exception info : {}", e.getMessage());
        }
    }

    public interface Runnable<T> {
        T run() throws Exception;
    }

    /**
     * 接口将由任何需要使用RedisLock分布式锁的类实现
     */
    public abstract static class RedisLockRunnable {

        /**
         * 锁的名称
         *
         * @return 锁名称
         */
        protected abstract String getLockKey();

        /**
         * 设置锁的时间
         *
         * @return 锁时间
         */
        protected long getLockTime() {
            return maxWaitTime;
        }

        /**
         * 获锁后进行操作的业务
         *
         * @return String
         * @throws Exception
         */
        protected abstract String run() throws Exception;
    }

}