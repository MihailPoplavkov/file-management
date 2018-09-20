package services

import java.io.{File, InputStream}
import java.util.UUID

import com.amazonaws.SdkBaseException
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import scala.util.Try

@Singleton
class S3FileManager(client: AmazonS3, configuration: Configuration) extends FileManager {

  type Id = UUID

  private val bucketName: String = configuration.get[String]("aws.s3.bucket.name")

  // inject here, because we need a constructor with AmazonS3 argument for tests
  @Inject() def this(configuration: Configuration) =
    this(AmazonS3ClientBuilder.defaultClient(), configuration)

  override def upload(file: File): Either[SdkBaseException, UUID] =
    callS3 {
      val id = UUID.randomUUID()
      client.putObject(bucketName, id.toString, file)
      id
    }

  override def download(id: UUID): Either[SdkBaseException, InputStream] =
    callS3 {
      val obj: S3Object = client.getObject(bucketName, id.toString)
      obj.getObjectContent
    }

  override def remove(id: UUID): Either[SdkBaseException, Boolean] =
    callS3 {
      if (client.doesObjectExist(bucketName, id.toString)) {
        client.deleteObject(bucketName, id.toString)
        true
      } else {
        false
      }
    }

  private def callS3[T](call: => T): Either[SdkBaseException, T] = {
    Try(call).toEither.left.map {
      case sbe: SdkBaseException => sbe
      case e => throw e
    }
  }
}
