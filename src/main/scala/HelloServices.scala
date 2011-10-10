import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.BijectionsChunkString


trait HelloServices extends BlueEyesServiceBuilder with HttpRequestCombinators with BijectionsChunkString {
    val hello = service("hello", "0.1") { context =>
        request {
            path("/") {
                produce(text/html) {
                    get { request =>
                        val content = <html>
                                         <body>hello, world</body>
                                      </html>
                        val response = HttpResponse[String](content = Some(content.buildString(true)))
                        Future.sync(response)
                     }
                }
            }
        }
    }
}


object AppServer extends BlueEyesServer with HelloServices {
  override def main(args: Array[String]) = super.main(Array("--configFile", "server.conf"))
}
