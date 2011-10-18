package blueeyes.persistence.mongo

import net.lag.configgy.ConfigMap
import com.mongodb.{DB, MongoURI}

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

  override def database(databaseName: String) = {
    val db: DB = mongo.getDB(databaseName)
    db.authenticate(mongoURI.getUsername, mongoURI.getPassword)
    new RealDatabase(this, db)
  }

}