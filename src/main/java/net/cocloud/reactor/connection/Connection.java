package net.cocloud.reactor.connection;

import net.cocloud.reactor.EventLoop;

import java.nio.channels.SocketChannel;

public interface Connection {

    EventLoop eventLoop();

    SocketChannel channel();

    ConnectionPipeline pipeline();

    void read();

    void write();
}
