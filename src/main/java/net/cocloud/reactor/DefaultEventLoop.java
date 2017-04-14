package net.cocloud.reactor;

import net.cocloud.reactor.common.ReactorException;
import net.cocloud.reactor.connection.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultEventLoop implements EventLoop, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventLoop.class);

    private EventLoopGroup parent;
    private String name;
    private Selector selector;

    private List<Connection> connections = new LinkedList<>();

    private Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    public DefaultEventLoop(EventLoopGroup parent, String name) {
        this.parent = parent;
        this.name = name;

        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            logger.error("init select error.", e);
            throw new ReactorException(e);
        }
    }

    @Override
    public void run() {
        final Selector selector = this.selector;
        Set<SelectionKey> selectedKeys = null;

        for (; ; ) {
            try {
                while (!taskQueue.isEmpty()) {
                    Runnable task = taskQueue.poll();
                    task.run();
                }

                selector.select(1000L);

                selectedKeys = selector.selectedKeys();

                if (selectedKeys.isEmpty()) {
                    continue;
                }

                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    Connection connection = (Connection) key.attachment();

                    if (connection != null && key.isValid()) {
                        int readyOps = key.readyOps();

                        if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
                            ((SocketChannel) key.channel()).finishConnect();
                        }

                        // write prior to read
                        if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                            connection.write();
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                        }

                        if ((readyOps & (SelectionKey.OP_READ)) != 0) {
                            connection.read();
                        }

                    } else {
                        key.cancel();
                    }
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void register(Connection connection) {
        Objects.requireNonNull(connection);

        taskQueue.add(new RegisterTask(connection));
    }

    private class RegisterTask implements Runnable {

        private Connection connection;

        public RegisterTask(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            connections.add(connection);

            try {
                connection.channel().register(selector, SelectionKey.OP_READ, connection);

                logger.info("{} connected.", connection.channel().getRemoteAddress());

                connection.pipeline().fireRegistered();

            } catch (IOException e) {
                logger.error("channel register error.", e);
                throw new ReactorException(e);
            }
        }
    }
}
