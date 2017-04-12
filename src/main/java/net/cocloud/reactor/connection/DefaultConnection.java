package net.cocloud.reactor.connection;

import net.cocloud.reactor.EventLoop;
import net.cocloud.reactor.common.ReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DefaultConnection implements Connection {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnection.class);

    private EventLoop eventLoop;
    private SocketChannel channel;
    private ConnectionPipeline pipeline;

    private Queue<ByteBuffer> readBufferQueue = new ConcurrentLinkedQueue<>();
    private Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<>();

    public DefaultConnection(EventLoop eventLoop, SocketChannel channel) {
        Objects.requireNonNull(eventLoop);
        Objects.requireNonNull(channel);

        this.eventLoop = eventLoop;
        this.channel = channel;
        this.pipeline = new DefaultConnectionPipeline(this);
    }

    @Override
    public void read() {
        ByteBuffer buffer = ByteBuffer.allocate(4096);

        final SocketChannel channel = this.channel;
        try {
            int count = channel.read(buffer);
            if (count > 0) {
                buffer.flip();
                readBufferQueue.add(buffer);
                writeBufferQueue.add(ByteBuffer.wrap(buffer.array(), 0, buffer.limit()));
                logger.info("connection {} read {} bytes", this.toString(), buffer.limit());

                pipeline.fireRead(buffer);
            } else if (count == -1) {
                logger.info("connection {} close", this.toString());
                channel.close();
            }
        } catch (IOException e) {
            throw new ReactorException(e);
        }
    }

    @Override
    public void write() {
        final SocketChannel channel = this.channel;
        try {
            while (!writeBufferQueue.isEmpty()) {
                ByteBuffer buffer = writeBufferQueue.poll();

                channel.write(buffer);
                logger.info("connection {} write {} bytes", this.toString(), buffer.limit());

                pipeline.fireWrite(buffer);
            }
        } catch (IOException e) {
            throw new ReactorException(e);
        }
    }

    @Override
    public EventLoop eventLoop() {
        return eventLoop;
    }

    @Override
    public SocketChannel channel() {
        return channel;
    }

    @Override
    public ConnectionPipeline pipeline() {
        return pipeline;
    }
}
