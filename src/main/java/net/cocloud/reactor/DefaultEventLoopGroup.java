package net.cocloud.reactor;

import net.cocloud.reactor.connection.Connection;

public class DefaultEventLoopGroup implements EventLoopGroup {

    private EventLoop[] eventLoops;
    private volatile int currEventLoopIdx;

    public DefaultEventLoopGroup(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("threads <= 0");
        }

        eventLoops = new EventLoop[threads];

        for (int i = 0; i < threads; ++i) {
            eventLoops[i] = new DefaultEventLoop(this, "eventloop-" + i);
        }

        for (int i = 0; i < threads; ++i) {
            new Thread(eventLoops[i]).start();
        }

        currEventLoopIdx = 0;
    }

    @Override
    public synchronized EventLoop next() {
        return eventLoops[currEventLoopIdx++ % eventLoops.length];
    }

    @Override
    public void register(Connection connection) {
        connection.eventLoop().register(connection);
    }
}
