-- 其实只有2个key，一个摘要key，一个对象keu
local key = KEYS[1]
local name = ARGV[1]

local locker = redis.call('get', key)
if locker == name then
    redis.call('del', key)
end
