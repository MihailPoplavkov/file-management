package utils

import slick.jdbc.meta.MTable
import utils.DbProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object CreateDB extends App {
  val db = Database.forConfig("db")
  val linkTable = TableQuery[LinkTable]
  val dropTableAction = linkTable.schema.drop
  val createTableAction = linkTable.schema.create
  val future = for {
    vect <- db.run(MTable.getTables)
    _ <- if (vect.exists(_.name.name == "links")) db.run(dropTableAction) else Future.unit
    _ <- db.run(createTableAction)
  } yield ()

  Await.result(future, 10.second)
}
