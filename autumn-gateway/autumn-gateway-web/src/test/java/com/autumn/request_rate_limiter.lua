--存放令牌的数量
local tokens_key = KEYS[1]
-- 存放最近一次取灵摆的时间
local timestamp_key = KEYS[2]
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

--每秒钟向桶中注入令牌的数量
local rate = tonumber(ARGV[1])
--桶的最大容量
local capacity = tonumber(ARGV[2])
-- 当前时间
local now = tonumber(ARGV[3])
-- 每次算几个令牌
local requested = tonumber(ARGV[4])

--从零开始多久满
local fill_time = capacity / rate
-- key的存活时间，如果过了这个时间 肯定就慢了 没有必要存
local ttl = math.floor(fill_time * 2)

--redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
--redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
--redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
--redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
--redis.log(redis.LOG_WARNING, "ttl " .. ttl)

-- 目前桶的立令牌数
local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
    last_tokens = capacity
end
--redis.log(redis.LOG_WARNING, "last_tokens " .. last_tokens)

--最近一次取令牌的时间
local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
    last_refreshed = 0
end
--redis.log(redis.LOG_WARNING, "last_refreshed " .. last_refreshed)

--上次取令牌到现在过了多少秒
local delta = math.max(0, now - last_refreshed)
-- 放新令牌后的总数
local filled_tokens = math.min(capacity, last_tokens + (delta * rate))
-- 现在桶里令牌够不够执行一次
local allowed = filled_tokens >= requested
-- 当前令牌数
local new_tokens = filled_tokens
local allowed_num = 0
if allowed then
    -- 拿走一次令牌后剩余令牌数
    new_tokens = filled_tokens - requested
    allowed_num = 1
end

--redis.log(redis.LOG_WARNING, "delta " .. delta)
--redis.log(redis.LOG_WARNING, "filled_tokens " .. filled_tokens)
--redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)
--redis.log(redis.LOG_WARNING, "new_tokens " .. new_tokens)
-- 更新令牌数
redis.call("setex", tokens_key, ttl, new_tokens)
-- 更新最后一次执行时间
redis.call("setex", timestamp_key, ttl, now)

--允许的数量  当前的令牌数
return { allowed_num, new_tokens }
--
--令牌桶算法(Token Bucket)和 Leaky Bucket 效果一样但方向相反的算法,更加容易理解
--1随着时间流逝,系统会按恒定1/QPS时间间隔(如果QPS=100,则间隔是10ms)
--往桶里加入Token(想象和漏洞漏水相反,有个水龙头在不断的加水),
--如果桶已经满了就不再加了.新请求来临时,会各自拿走一个Token,如果没有Token可拿了就阻塞或者拒绝服务.