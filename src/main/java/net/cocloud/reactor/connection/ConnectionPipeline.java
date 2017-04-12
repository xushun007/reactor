package net.cocloud.reactor.connection;

import net.cocloud.reactor.handler.ConnectionHandler;

public interface ConnectionPipeline {

    Connection connection();

    ConnectionPipeline add(String name, ConnectionHandler handler);

    ConnectionPipeline remove(String name);

    ConnectionPipeline fireRegistered();

    ConnectionPipeline fireUnregistered();

    ConnectionPipeline fireRead(Object msg);

    ConnectionPipeline fireWrite(Object msg);

    ConnectionPipeline flush();

}
