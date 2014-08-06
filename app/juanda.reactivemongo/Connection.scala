package juanda.reactivemongo

import java.util

import reactivemongo.api.MongoDriver

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

case class Connection(servers: util.List[String], dbName: String){

  def get = {
    val driver = new MongoDriver()
    val connection = driver.connection(servers.asScala)
    val db = connection.db(dbName)

    db
  }
}
