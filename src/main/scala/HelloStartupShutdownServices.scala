import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpResponse, HttpRequest}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{BijectionsChunkString, ByteChunk}

trait HelloStartupShutdownServices extends BlueEyesServiceBuilder with HttpRequestCombinators with BijectionsChunkString {
  val helloStartupShutdown= service("helloHtml", "0.1") { context =>
    startup {
      println("startup")
      Future.sync(())
    } ->
    request {
      println("request")
      path("/hello") {
        println("path /")
        produce(text/html) {
          println("produce html")
          get { request: HttpRequest[ByteChunk] =>
            println("get")
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
      println("shutdown")
      Future.sync(())
    }
  }
}
