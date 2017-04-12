package net.cocloud.reactor.connection;

import net.cocloud.reactor.EventLoop;

import java.nio.channels.SocketChannel;

public class DefaultConnectionFactory implements ConnectionFactory {

    @Override
    public Connection newConnection(EventLoop eventLoop, SocketChannel channel) {
        return new DefaultConnection(eventLoop, channel);
    }
}
