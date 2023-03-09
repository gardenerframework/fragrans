--这个逻辑是用反向索引找到id，然后读取令牌
return redis.call('incr', KEYS[1])