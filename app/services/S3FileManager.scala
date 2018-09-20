package services

import java.io.{File, FileOutputStream}
import java.util.UUID

import com.amazonaws.SdkBaseException
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.util.IOUtils
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import scala.util.Try

@Singleton
class S3FileManager(client: AmazonS3, configuration: Configuration) extends FileManager {

  type Id = UUID

  private val bucketName: String = configuration.get[String]("aws.s3.bucket.name")

  // inject here, because we need a constructor with AmazonS3 argument for tests
  @Inject() def this(configuration: Configuration) =
    this(
      AmazonS3ClientBuilder.standard()
        .withRegion(configuration.get[String]("aws.s3.bucket.region"))
        .build(),
      configuration)

  override def upload(file: File): Either[SdkBaseException, UUID] =
    callS3 {
      val id = UUID.randomUUID()
      client.putObject(bucketName, id.toString, file)
      file.delete()
      id
    }

  override def download(id: UUID): Either[SdkBaseException, File] =
    callS3 {
      val obj = client.getObject(bucketName, id.toString)
      val file = File.createTempFile("fm/", obj.getKey)
      IOUtils.copy(obj.getObjectContent, new FileOutputStream(file))
      file
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

  override def parseStringToId(s: String): Either[IllegalArgumentException, UUID] =
    Try(UUID.fromString(s)).toEither.left.map {
      case e: IllegalArgumentException => e
      case e => throw e
    }

  private def callS3[T](call: => T): Either[SdkBaseException, T] = {
    Try(call).toEither.left.map {
      case e: SdkBaseException => e
      case e => throw e
    }
  }
}
