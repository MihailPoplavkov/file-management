package utils

import java.time.Instant

import model.Link
import utils.DbProfile.api._

class LinkTable(tag: Tag) extends Table[Link](tag, "links") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def fileId = column[String]("file_id")

  def expired = column[Instant]("expired")

  override def * = (id.?, fileId, expired).mapTo[Link]
}
