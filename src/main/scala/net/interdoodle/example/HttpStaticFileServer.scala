package net.interdoodle.example

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory

object HttpStaticFileServer extends App {

  val bootstrap: ServerBootstrap = new ServerBootstrap(
    new NioServerSocketChannelFactory(
      Executors.newCachedThreadPool(),
      Executors.newCachedThreadPool()
    )
  );

  // Set up the event pipeline factory.
  bootstrap.setPipelineFactory(new HttpStaticFileServerPipelineFactory("src/main/webapp"));

  // Bind and start to accept incoming connections.
  bootstrap.bind(new InetSocketAddress(System.getenv("PORT").orElse("8080").asInstanceOf[Int]));
}