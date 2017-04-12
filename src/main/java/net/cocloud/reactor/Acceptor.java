package net.cocloud.reactor;

import net.cocloud.reactor.common.ReactorException;
import net.cocloud.reactor.connection.Connection;
import net.cocloud.reactor.connection.ConnectionFactory;
import net.cocloud.reactor.connection.DefaultConnectionFactory;
import net.cocloud.reactor.handler.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Acceptor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Acceptor.class);

    private final int port;
    private final Selector selector;
    private final ServerSocketChannel serverChannel;

    private final EventLoopGroup eventLoopGroup;
    private List<ConnectionHandler> handlers;

    private ConnectionFactory connectionFactory;

    //private ScheduledExecutorService scheduledExecutorService;

    public Acceptor(EventLoopGroup childGroup, final int port) {
        Objects.requireNonNull(childGroup, "childGroup");

        if (port <= 0) {
            throw new IllegalArgumentException("port");
        }

        this.eventLoopGroup = childGroup;
        this.port = port;

        try {
            this.selector = Selector.open();

            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.configureBlocking(false);
            this.serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            this.serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
            this.serverChannel.bind(new InetSocketAddress(this.port), 1024);
            this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT, this);
        } catch (IOException e) {
            logger.error("init select and channel error.", e);
            throw new ReactorException(e);
        }

        this.handlers = new LinkedList<>();

        this.connectionFactory = new DefaultConnectionFactory();

    }



    @Override
    public void run() {

        try {
            logger.info("Acceptor {} start to service...", serverChannel.getLocalAddress());
        }
        catch (IOException e) {
            throw new ReactorException(e);
        }

        final Selector selector = this.selector;

        for (; ; ) {
            try {
                selector.select(1000L);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        doAccept(key);
                    } else {
                        key.cancel();
                    }

                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
        channel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);

        Connection connection = connectionFactory.newConnection(eventLoopGroup.next(), channel);

        handlers.forEach(handler -> connection.pipeline().add(handler.toString(), handler));

        eventLoopGroup.register(connection);
    }

    public Acceptor addHandler(ConnectionHandler handler) {
        Objects.requireNonNull(handler);
        handlers.add(handler);

        return this;
    }

}
