package services

import java.io.{File, InputStream}
import java.util.UUID

import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class S3FileManager(client: AmazonS3, configuration: Configuration) extends FileManager {

  type Id = UUID

  private val bucketName: String = configuration.get[String]("aws.s3.bucket.name")

  // inject here, because we need a constructor with AmazonS3 argument for tests
  @Inject() def this(configuration: Configuration) =
    this(AmazonS3ClientBuilder.defaultClient(), configuration)

  override def upload(file: File): UUID = {
    val id = UUID.randomUUID()
    client.putObject(bucketName, id.toString, file)
    id
  }

  override def download(id: UUID): InputStream = {
    val obj: S3Object = client.getObject(bucketName, id.toString)
    obj.getObjectContent
  }

  override def remove(id: UUID): Boolean = {
    if (client.doesObjectExist(bucketName, id.toString)) {
      client.deleteObject(bucketName, id.toString)
      true
    } else {
      false
    }
  }
}
