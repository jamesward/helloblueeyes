package net.interdoodle.hbe

//import blueeyes._
import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpResponse, HttpRequest}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{BijectionsChunkString, ByteChunk}
import blueeyes.persistence.mongo.{ConfigurableMongo, Mongo, MongoSelectQuery}
import blueeyes.json.Printer;
import blueeyes.json.JsonAST.{JField, JString, JObject, JValue, JArray}
import net.lag.configgy.ConfigMap

trait HelloMongoServices extends BlueEyesServiceBuilder with HttpRequestCombinators with BijectionsChunkString with ConfigurableMongo {

    val helloMongo = service("helloMongo", "0.1") {
      logging { log => context =>
          startup {
            val mongoConfig = context.config.configMap("mongo")
            HelloConfig(mongoConfig, mongo(mongoConfig)).future
          } ->
          request { helloConfig: HelloConfig =>
              path("/mongo") {
                  produce(application/javascript) {
                      get { request =>
                        /*
                        {
                          "foo": "testing"
                        }
                        */
                        val s:JString = JString("testing")
                        val f:JField = JField("foo", s)
                        var o:JObject = JObject(List(f))
                        log.info(Printer.pretty(Printer.render(o)))
                        helloConfig.database(insert(o).into("ticks"))

                        helloConfig.database(selectAll.from("ticks")) map { records =>
                          HttpResponse[String](
                            content = Some("parseResponse(" + Printer.compact(Printer.render(JArray(records.toList))) + ");")
                          )
                        }
                      }
                  }
              }
          } ->
          shutdown { helloConfig: HelloConfig =>
            Future.sync(())
          }
      }
    }
}

case class HelloConfig(config: ConfigMap, mongo: Mongo) {
  val database    = mongo.database(config("databaseName"))
}