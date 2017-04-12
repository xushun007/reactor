package net.cocloud.reactor.example;

import net.cocloud.reactor.connection.ConnectionPipeline;
import net.cocloud.reactor.handler.AbstractConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class EchoServerHandler extends AbstractConnectionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);

    @Override
    public void connectionRead(ConnectionPipeline pipeline, Object msg) throws Exception {
        super.connectionRead(pipeline, msg);
        ByteBuffer buffer = (ByteBuffer)msg;
        pipeline.connection().channel().write(buffer);
        logger.info("EchoServerHandler connectionRead ...");
    }

    @Override
    public void connectionRegistered(ConnectionPipeline pipeline) throws Exception {
        super.connectionRegistered(pipeline);
        logger.info("EchoServerHandler register ...");
    }

    @Override
    public void write(ConnectionPipeline pipeline, Object msg) throws Exception {
        super.write(pipeline, msg);
        ByteBuffer buffer = (ByteBuffer)msg;
        pipeline.connection().channel().write(buffer);
        logger.info("EchoServerHandler write ...");
    }
}
