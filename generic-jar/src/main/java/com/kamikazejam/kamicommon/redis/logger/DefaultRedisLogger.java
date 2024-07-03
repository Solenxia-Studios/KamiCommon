package com.kamikazejam.kamicommon.redis.logger;

import com.kamikazejam.kamicommon.util.LoggerService;

public class DefaultRedisLogger extends LoggerService {

    @Override
    public String getLoggerName() {
        return "RedisManager";
    }

    @Override
    public boolean isDebug() {
        return true;
    }
}
