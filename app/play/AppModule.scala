package play

import com.google.inject.AbstractModule
import services.{FileManager, S3FileManager}

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[FileManager]).to(classOf[S3FileManager])
  }
}
