package blueeyes.persistence.mongo

import com.mongodb.MongoURI
import net.lag.configgy.ConfigMap

/**
 * Created by IntelliJ IDEA.
 * User: jamesw
 * Date: 10/18/11
 * Time: 12:16 AM
 * To change this template use File | Settings | File Templates.
 */

class EnvMongo(val mongoURI: MongoURI, val config: ConfigMap) extends RealMongo(config) {

  private lazy val mongo = {
    new com.mongodb.Mongo(mongoURI)
  }

  override def database(databaseName: String) = new RealDatabase(this, mongo.getDB(databaseName))

}