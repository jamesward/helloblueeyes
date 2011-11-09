package net.interdoodle.example

import blueeyes.core.service.HttpServer
import net.lag.configgy.Configgy
import java.util.concurrent.CountDownLatch
import util.Properties


/**
 * @author James Ward */

trait EnvHttpServer extends HttpServer { self =>

  override def main(args: Array[String]) {
    
    // build a config string
    val configString = "server.port = " + Properties.envOrElse("PORT", "8585") + "\n" +
                       "server.sslEnable = " + Properties.envOrElse("SSL_ENABLE", "false")
    
    Configgy.configureFromString(configString)
          
    start.deliverTo { _ =>
      Runtime.getRuntime.addShutdownHook { new Thread {
        override def start() {
          val doneSignal = new CountDownLatch(1)

          self.stop.deliverTo { _ =>
            doneSignal.countDown()
          }.ifCanceled { e =>
            doneSignal.countDown()
          }

          doneSignal.await()
        }
      }}
    }
  }
}