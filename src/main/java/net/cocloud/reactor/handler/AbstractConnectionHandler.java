package net.cocloud.reactor.handler;

import net.cocloud.reactor.connection.ConnectionPipeline;

public class AbstractConnectionHandler implements ConnectionHandler {

    @Override
    public void connectionRegistered(ConnectionPipeline pipeline) throws Exception {
        
    }

    @Override
    public void bind(ConnectionPipeline pipeline) throws Exception {

    }

    @Override
    public void connect(ConnectionPipeline pipeline) throws Exception {

    }

    @Override
    public void connectionUnregistered(ConnectionPipeline pipeline) throws Exception {

    }

    @Override
    public void disconnect(ConnectionPipeline pipeline) throws Exception {

    }

    @Override
    public void connectionRead(ConnectionPipeline pipeline, Object msg) throws Exception {

    }

    @Override
    public void close(ConnectionPipeline pipeline) throws Exception {

    }

    @Override
    public void exceptionCaught(ConnectionPipeline pipeline, Throwable cause) throws Exception {

    }

    @Override
    public void read(ConnectionPipeline pipeline) throws Exception {

    }

    @Override
    public void write(ConnectionPipeline pipeline, Object msg) throws Exception {

    }

    @Override
    public void flush(ConnectionPipeline pipeline) throws Exception {

    }
}
