package services

import java.time.Instant

import javax.inject.Singleton
import model.Link
import utils.DbProfile.api._
import utils.LinkTable

@Singleton
class LinkDAO {

  private val linkTable = TableQuery[LinkTable]

  def create(link: Link): DBIO[Link] =
    linkTable returning linkTable += link

  def getNotExpiredById(id: Long): DBIO[Option[Link]] =
    linkTable.filter(l => l.id === id && l.expired >= Instant.now()).result.headOption

  def deleteByFileId(fileId: String): DBIO[Int] =
    linkTable.filter(_.fileId === fileId).delete

}
