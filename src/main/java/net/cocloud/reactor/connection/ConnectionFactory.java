package net.cocloud.reactor.connection;

import net.cocloud.reactor.EventLoop;

import java.nio.channels.SocketChannel;

public interface ConnectionFactory {

    Connection newConnection(EventLoop eventLoop, SocketChannel channel);
}
