package net.cocloud.reactor;

import net.cocloud.reactor.connection.Connection;

public interface EventLoop extends Runnable {
    void register(Connection connection);
}
