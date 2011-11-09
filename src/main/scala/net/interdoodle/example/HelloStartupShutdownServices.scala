package net.interdoodle.example

import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpResponse, HttpRequest}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.data.{BijectionsChunkString, ByteChunk}


/** Sample BlueEyes service, showing all three phases: startup, processing and shutdown
 * @author James Ward
 * @author Mike Slinn */

trait HelloStartupShutdownServices extends BlueEyesServiceBuilder with BijectionsChunkString {
  val helloStartupShutdown= service("helloStartupShutdown", "0.1") {
    logging { log => context =>
      startup {
        log.info("startup")
        Future.sync(())
      } ->
      request {
        path("/hello") {
          produce(text/html) {
            get { request: HttpRequest[ByteChunk] =>
              log.info("get")
              val content = <html>
                               <body>Hello, world!</body>
                            </html>
              val response = HttpResponse[String](content = Some(content.buildString(true)))
              Future.sync(response)
            }
          }
        }
      } ->
      shutdown {
        log.info("shutdown")
        Future.sync(())
      }
    }
  }
}
