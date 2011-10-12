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

trait HelloJsonServices extends BlueEyesServiceBuilder
  with HttpRequestCombinators with BijectionsChunkString
  with BijectionsChunkJson {
    val helloJson:HttpService[ByteChunk] = service("helloJson", "0.1") {
      context:HttpServiceContext =>
      request {
        path("/json") {
          jvalue {
            get { requestParam:HttpRequest[JValue] =>
               val json = JString("Hello World!")
               val response = HttpResponse[JValue](content = Some(json))
               println(response);
               Future.sync(response)
            }
          }
        }
      }
   }
}
