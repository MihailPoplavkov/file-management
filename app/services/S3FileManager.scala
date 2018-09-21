package services

import java.io.{File, FileOutputStream}
import java.util.UUID

import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.util.IOUtils
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import services.FileManager.{FileManagerException, FileNotFoundException, IncorrectIdFormatException, OtherException}

import scala.util.Try

@Singleton
class S3FileManager(client: AmazonS3, configuration: Configuration) extends FileManager {

  private val FileName = "file_name"

  type Id = UUID

  private val bucketName: String = configuration.get[String]("aws.s3.bucket.name")

  // inject here, because we need a constructor with AmazonS3 argument for tests
  @Inject() def this(configuration: Configuration) =
    this(
      AmazonS3ClientBuilder.standard()
        .withRegion(configuration.get[String]("aws.s3.bucket.region"))
        .build(),
      configuration)

  override def upload(file: File, name: String): Either[FileManagerException, UUID] =
    callExceptionally {
      val id = UUID.randomUUID()
      val metaName = new ObjectMetadata()
      metaName.addUserMetadata(FileName, name)
      val request = new PutObjectRequest(bucketName, id.toString, file).withMetadata(metaName)
      client.putObject(request)
      file.delete()
      id
    }

  override def download(id: UUID): Either[FileManagerException, (File, String)] =
    ifExists(id) {
      val obj = client.getObject(bucketName, id.toString)
      val name = obj.getObjectMetadata.getUserMetadata.getOrDefault(FileName, "untitled")
      val file = File.createTempFile("fm/", obj.getKey)
      IOUtils.copy(obj.getObjectContent, new FileOutputStream(file))
      (file, name)
    }

  override def remove(id: UUID): Either[FileManagerException, Unit] =
    ifExists(id) {
      client.deleteObject(bucketName, id.toString)
    }


  override def parseStringToId(s: String): Either[IncorrectIdFormatException, UUID] =
    Try(UUID.fromString(s)).toEither.left.map(_ => IncorrectIdFormatException(s))

  private def callExceptionally[T](call: => T): Either[FileManagerException, T] = {
    Try(call).toEither.left.map(e => OtherException(e.getMessage))
  }

  private def ifExists[T](id: UUID)(call: => T): Either[FileManagerException, T] = {
    for {
      isExists <- callExceptionally(client.doesObjectExist(bucketName, id.toString))
      res <- if (isExists) callExceptionally(call) else Left(FileNotFoundException(id.toString))
    } yield res
  }
}
