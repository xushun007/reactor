package net.cocloud.reactor.example;

import net.cocloud.reactor.Acceptor;
import net.cocloud.reactor.DefaultEventLoopGroup;
import net.cocloud.reactor.EventLoopGroup;

public class EchoServerDemo {

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new DefaultEventLoopGroup(2);
        Acceptor acceptor = new Acceptor(eventLoopGroup, 12345);

        acceptor.addHandler(new EchoServerHandler());

        acceptor.start();
    }
}
