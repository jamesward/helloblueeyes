package blueeyes.persistence.mongo

import net.lag.configgy.ConfigMap
import com.mongodb.{DB, MongoURI}

/**
 * @author James Ward */

class EnvMongo(val mongoURI: MongoURI, val config: ConfigMap) extends RealMongo(config) {

  private lazy val mongo = new com.mongodb.Mongo(mongoURI)

  override def database(databaseName: String) = {
    val db: DB = mongo.getDB(databaseName)
    if ((mongoURI.getUsername != null) && (mongoURI.getPassword != null))
      db.authenticate(mongoURI.getUsername, mongoURI.getPassword)
    new RealDatabase(this, db)
  }
}