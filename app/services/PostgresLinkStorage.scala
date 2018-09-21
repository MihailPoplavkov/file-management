package services

import java.time.Instant

import javax.inject.{Inject, Singleton}
import model.Link
import utils.DbProfile.api._

import scala.concurrent.Future

@Singleton
class PostgresLinkStorage @Inject()(dao: LinkDAO) extends LinkStorage {

  private val db = Database.forConfig("db")

  override def create(fileId: String, seconds: Long): Future[Link] =
    db.run(dao.create(Link(fileId = fileId, expired = Instant.now().plusSeconds(seconds))))

  override def getNotExpiredById(id: Long): Future[Option[Link]] =
    db.run(dao.getNotExpiredById(id))

  override def deleteByFileId(fileId: String): Future[Int] =
    db.run(dao.deleteByFileId(fileId))
}
