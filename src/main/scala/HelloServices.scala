import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{BijectionsChunkString, ByteChunk}
import blueeyes.core.service.{HttpService, HttpServiceContext}
import blueeyes.json.JsonAST._
import blueeyes.core.data.{ByteChunk, BijectionsChunkJson}
import blueeyes.persistence.mongo.{ConfigurableMongo, MongoFilterAll, Mongo, MongoFilter}

trait HelloServices extends BlueEyesServiceBuilder with HttpRequestCombinators
       with BijectionsChunkString with BijectionsChunkJson with ConfigurableMongo {
    val hello:HttpService[ByteChunk] = service("hello", "0.1") { context:HttpServiceContext =>
      startup {
        Future.sync(()) /* return a future of unit */
      } ->
      request { state:Unit=> /* request function accepts a fn as a parameter and accepts a state and returns a request handler */
        path("/") {
          jvalue {
            get { requestParam:HttpRequest[JValue] =>
               val json = JString("Hello World!")
               val response = HttpResponse[JValue](content = Some(json))
               println(response);
               Future.sync(response)
            }
          }
        }
      } ->
      shutdown { state =>
        Future.sync(()) /* access state */
      }
   }
}


object AppServer extends BlueEyesServer with HelloServices {
  override def main(args: Array[String]) = super.main(Array("--configFile", "server.conf"))
}
