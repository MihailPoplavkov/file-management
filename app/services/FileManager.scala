package services

import java.io.{File, InputStream}

trait FileManager {

  type Id

  def upload(file: File): Either[Throwable, Id]

  def download(id: Id): Either[Throwable, InputStream]

  def remove(id: Id): Either[Throwable, Boolean]

}
