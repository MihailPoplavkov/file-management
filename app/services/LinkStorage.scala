package services

import model.Link

import scala.concurrent.Future

trait LinkStorage {

  def create(fileId: String, seconds: Long): Future[Link]

  def getNotExpiredById(id: Long): Future[Option[Link]]

  def deleteByFileId(fileId: String): Future[Int]

}
