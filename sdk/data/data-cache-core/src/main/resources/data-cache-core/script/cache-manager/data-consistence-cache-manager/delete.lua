-- 其实只有2个key，一个摘要key，一个对象keu
local id = KEYS[1]
local digestKey = KEYS[2]

redis.call('del', id)
redis.call('del', digestKey)