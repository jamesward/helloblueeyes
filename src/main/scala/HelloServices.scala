import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{BijectionsChunkString, ByteChunk}
import blueeyes.core.service.{HttpService, HttpServiceContext}

trait HelloServices extends BlueEyesServiceBuilder with HttpRequestCombinators with BijectionsChunkString {
    val hello:HttpService[ByteChunk] = service("hello", "0.1") { context:HttpServiceContext =>
        request/*:AsyncHttpService[ByteChunk]*/ {
            path("/")/*:HttpServices.HttpService[ByteChunk, Future[HttpResponse[ByteChunk]]]*/ {
                produce(text/html) {
                    get { request =>
                        val content = <html>
                                         <body>hello, world</body>
                                      </html>
                        val response = HttpResponse[String](content = Some(content.buildString(true)))
                        println(response);
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
