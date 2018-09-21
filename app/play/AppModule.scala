package play

import com.google.inject.AbstractModule
import services.{FileManager, LinkStorage, PostgresLinkStorage, S3FileManager}

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[FileManager]).to(classOf[S3FileManager])
    bind(classOf[LinkStorage]).to(classOf[PostgresLinkStorage])
  }
}
