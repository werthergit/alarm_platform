--KEYS[1]: 限流 key
--KEYS[2]: silencePeriod key
--ARGV[1]: 时间戳 - 时间窗口
--ARGV[2]: 当前时间戳（作为score）
--ARGV[3]: 阈值
--ARGV[4]: score 对应的唯一value
--ARGV[5]: silencePeriod 静默时长
-- 1. 移除时间窗口之前的数据
redis.call('zremrangeByScore', KEYS[1], 0, ARGV[1])
-- 2、插入数据
redis.call('zadd', KEYS[1], ARGV[2], ARGV[4])
-- 3. 统计当前元素数量
local res = redis.call('zcard', KEYS[1])
-- 4. 是否超过阈值
if (res == nil) or (res < tonumber(ARGV[3])) then
    return 0
else
    -- 5. 静默处理
    local exist = redis.call("GET", KEYS[2]);
    if not exist  then
        redis.call("SETEX", KEYS[2], tonumber(ARGV[5]), "1");
        return 1
    else
        -- 静默
        return -1
    end

end
