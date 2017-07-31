import com.google.inject.AbstractModule
import domain.{Athlete, Point, Segment, SegmentEffort}
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}
import services.actors._

class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[LoadDataActor]("loadDataActor")
    bindActor[ActivitiesActor]("activitiesActor")
    bindActor[SegmentsActor]("segmentsActor")
    bindActor[UpdateDatabaseActor]("updateDatabaseActor")
    bindActor[ReadDatabaseActor]("readDatabaseActor")

    import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
    import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
    import org.mongodb.scala.bson.codecs.Macros._

    val uri: String = configuration.get[String]("database.uri")
    val databaseName: String = configuration.get[String]("database.name")

    System.setProperty("org.mongodb.async.type", "netty")

    val codecRegistry = fromRegistries(
      fromProviders(classOf[Point]),
      fromProviders(classOf[Athlete]),
      fromProviders(classOf[Segment]),
      fromProviders(classOf[SegmentEffort]),
      DEFAULT_CODEC_REGISTRY)

    val client: MongoClient = MongoClient(uri)
    val db: MongoDatabase = client.getDatabase(databaseName).withCodecRegistry(codecRegistry)

    bind(classOf[MongoDatabase]).toInstance(db)
  }
}
