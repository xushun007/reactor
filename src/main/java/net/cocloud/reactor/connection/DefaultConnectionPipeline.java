package net.cocloud.reactor.connection;

import net.cocloud.reactor.common.ReactorException;
import net.cocloud.reactor.handler.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultConnectionPipeline implements ConnectionPipeline {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionPipeline.class);

    private Connection connection;
    private Map<String, ConnectionHandler> handlerMap = new LinkedHashMap<>();

    public DefaultConnectionPipeline(Connection connection) {
        Objects.requireNonNull(connection);

        this.connection = connection;
    }


    @Override
    public Connection connection() {
        return connection;
    }

    @Override
    public synchronized ConnectionPipeline add(String name, ConnectionHandler handler) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(handler);

        handlerMap.put(name, handler);

        return this;
    }

    @Override
    public synchronized ConnectionPipeline remove(String name) {
        Objects.requireNonNull(name);

        handlerMap.remove(name);
        return this;
    }

    @Override
    public ConnectionPipeline fireRegistered() {
        handlerMap.forEach((name, handler) -> {
            logger.info("handler {} begin to call register()", name);

            try {
                handler.connectionRegistered(this);
            }
            catch (Exception e) {
                throw new ReactorException(e);
            }
        });

        return this;
    }

    @Override
    public ConnectionPipeline fireUnregistered() {
        return null;
    }

    @Override
    public ConnectionPipeline fireRead(Object msg) {
        handlerMap.forEach((name, handler) -> {
            logger.info("handler {} begin to call read()", name);

            try {
                handler.connectionRead(this, msg);
            }
            catch (Exception e) {
                throw new ReactorException(e);
            }
        });

        return this;
    }

    @Override
    public ConnectionPipeline fireWrite(Object msg) {
        handlerMap.forEach((name, handler) -> {
            logger.info("handler {} begin to call write()", name);

            try {
                handler.write(this, msg);
            }
            catch (Exception e) {
                throw new ReactorException(e);
            }
        });

        return this;
    }

    @Override
    public ConnectionPipeline flush() {
        return null;
    }
}
