package net.interdoodle.example.httpstaticfileserver

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import util.Properties

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
  bootstrap.bind(new InetSocketAddress(Properties.envOrElse("PORT", "9090").toInt));

}