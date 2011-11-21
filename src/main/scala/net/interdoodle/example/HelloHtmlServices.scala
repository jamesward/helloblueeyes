package net.interdoodle.example

import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.data.{ByteChunk, BijectionsChunkString}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.service.HttpServiceContext


/** Simple HTML service
 * @author James Ward */

trait HelloHtmlServices extends BlueEyesServiceBuilder with BijectionsChunkString {
  val helloHtml = service("helloHtml", "0.1") { context =>
      request {
        path("/") {
          produce(text/html) {
            get { request =>
              val content = <html>
                <body>Hello, world!</body>
              </html>
              val response = HttpResponse(content = Some(content.buildString(true)))
              Future.sync(response)
          }
        }
      }
    }
  }
}