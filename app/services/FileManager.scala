package services

import java.io.File

import services.FileManager.FileManagerException

trait FileManager {

  type Id

  def upload(file: File, name: String): Either[FileManagerException, Id]

  def download(id: Id): Either[FileManagerException, (File, String)]

  def remove(id: Id): Either[FileManagerException, Unit]

  def parseStringToId(s: String): Either[FileManagerException, Id]

}

object FileManager {

  sealed trait FileManagerException

  case class FileNotFoundException(id: String) extends FileManagerException

  case class IncorrectIdFormatException(id: String) extends FileManagerException

  case class OtherException(msg: String) extends FileManagerException

}
