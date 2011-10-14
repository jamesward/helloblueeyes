package net.interdoodle.hbe

import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.BijectionsChunkString


trait HelloHtmlServices extends BlueEyesServiceBuilder with HttpRequestCombinators with BijectionsChunkString {
  val helloHtml = service("helloHtml", "0.1") {
    context =>
      request {
        path("/") {
          produce(text / html) {
            get {
              request =>
                val content = <html>
                  <body>Hello, world!</body>
                </html>
                val response = HttpResponse[String](content = Some(content.buildString(true)))
                Future.sync(response)
            }
          }
        }
      }
  }
}
