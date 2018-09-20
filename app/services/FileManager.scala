package services

import java.io.File

trait FileManager {

  type Id

  def upload(file: File): Either[Throwable, Id]

  def download(id: Id): Either[Throwable, File]

  def remove(id: Id): Either[Throwable, Boolean]

  def parseStringToId(s: String): Either[IllegalArgumentException, Id]

}
