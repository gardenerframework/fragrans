-- 其实只有2个key，一个摘要key，一个对象keu
local id = KEYS[1]
local digestKey = KEYS[2]
local ttl=ARGV[1]

redis.call('expire', id, ttl)
redis.call('expire', digestKey, ttl)