--稍微注释一下以防忘掉
--key有2个，一个是摘要，一个是对象数据
--值有2-3个，一个是摘要内容，一个是数据内容，最后一个是ttl
local id = KEYS[1]
local digestKey = KEYS[2]
local content = ARGV[1]
local digest = ARGV[2]
local ttl = ARGV[3]
local done
if ttl == nil then
    done = redis.call('set', id, content, 'xx');
    if done then
        redis.call('set', digestKey, digest)
    end
else
    done = redis.call('set', id, content, 'xx', 'ex', ttl);
    if done then
        redis.call('set', digestKey, digest, 'ex', ttl)
    end
end
return done;
