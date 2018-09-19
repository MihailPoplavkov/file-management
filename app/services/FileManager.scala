package services

import java.io.{File, InputStream}

trait FileManager {

  type Id

  def upload(file: File): Id

  def download(id: Id): InputStream

  def remove(id: Id): Boolean

}
