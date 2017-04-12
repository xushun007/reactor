package net.cocloud.reactor;

import net.cocloud.reactor.connection.Connection;

public interface EventLoopGroup {

    EventLoop next();

    void register(Connection connection);

}
