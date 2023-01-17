--稍微注释一下以防忘掉
--key有2个，一个是摘要，一个是对象数据
--值有2-3个，一个是摘要内容，一个是数据内容，最后一个是ttl
local id = KEYS[1]
local digestKey = KEYS[2]
local content = ARGV[1]
local digest = ARGV[2]
local ttl = ARGV[3]
if ttl == nil then
    redis.call('set', id, content)
    redis.call('set', digestKey, digest)
else
    redis.call('set', id, content, 'ex', ttl)
    redis.call('set', digestKey, digest, 'ex', ttl)
end