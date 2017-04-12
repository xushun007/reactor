package net.cocloud.reactor.handler;

import net.cocloud.reactor.connection.ConnectionPipeline;

public interface ConnectionOutHandler {

    void bind(ConnectionPipeline pipeline) throws Exception;

    void connect(ConnectionPipeline pipeline) throws Exception;

    void disconnect(ConnectionPipeline pipeline) throws Exception;

    void close(ConnectionPipeline pipeline) throws Exception;

    void read(ConnectionPipeline pipeline) throws Exception;

    void write(ConnectionPipeline pipeline, Object msg) throws Exception;

    void flush(ConnectionPipeline pipeline) throws Exception;
}
