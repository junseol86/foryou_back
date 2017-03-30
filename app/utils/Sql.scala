package utils

/**
  * Created by Hyeonmin on 2017-03-29.
  */

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

class Sql @Inject()(db: Database, common: Common) {

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def getList(table: String, condition: String, page: Int, pageSize: Int): Map[String, Any] = {
    var list = List[Map[String, Any]]()
    var count = List[Map[String, Any]]()
    var result = Map[String, Any]()

    val pageOffset = page * pageSize

    val commonQuery =
      f"""FROM $table%s $condition%s"""
    val getListQuery =
      f"""SELECT * $commonQuery%s ORDER BY id DESC LIMIT $pageOffset%d, $pageSize%d"""
    val countQuery =
      f"""SELECT count(*) as total $commonQuery%s"""

    db.withConnection{implicit conn =>
      list = SQL(
        getListQuery.stripMargin).as(parser.*)
    }
    db.withConnection{implicit conn =>
      count = SQL(
        countQuery.stripMargin).as(parser.*)
    }

    result += "list" -> list
    result += "total" -> count(0)(".total")
    result += "pageSize" -> pageSize

    result
  }

  def deleteAContent(table: String, id: Int): Int = {
    db.withConnection { implicit conn =>
      SQL(
        s"DELETE FROM $table WHERE id = $id"
      ).executeUpdate()
    }
  }

}
