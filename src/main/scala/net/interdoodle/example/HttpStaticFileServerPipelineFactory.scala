package net.interdoodle.example

import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}
import org.jboss.netty.handler.codec.http.{HttpResponseEncoder, HttpChunkAggregator, HttpRequestDecoder}
import org.jboss.netty.handler.stream.ChunkedWriteHandler

class HttpStaticFileServerPipelineFactory(val dirName: String) extends ChannelPipelineFactory {

  def getPipeline(): ChannelPipeline = {
    val pipeline: ChannelPipeline = Channels.pipeline()
    pipeline.addLast("decoder", new HttpRequestDecoder())
    pipeline.addLast("aggregator", new HttpChunkAggregator(65536))
    pipeline.addLast("encoder", new HttpResponseEncoder())
    pipeline.addLast("chunkedWriter", new ChunkedWriteHandler())
    pipeline.addLast("handler", new HttpStaticFileServerHandler(dirName))
    pipeline
  }

}