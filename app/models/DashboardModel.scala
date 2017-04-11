package models

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

/**
  * Created by Hyeonmin on 2017-04-11.
  */
class DashboardModel @Inject()(db: Database) {
  val PAGE_SIZE = 5

  def getArticles: Map[String, Any] = {
    val parser: RowParser[Map[String, Any]] =
      SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
        Right(map + (meta.column.qualified -> value))
      }

    var getFieldsResult = List[Map[String, Any]]()
    val getFieldsQuery = f"""SELECT id, title FROM fields ORDER BY id DESC LIMIT $PAGE_SIZE%d"""
    db.withConnection{implicit conn =>
      getFieldsResult = SQL(
        getFieldsQuery.stripMargin).as(parser.*)
    }
    var getTaxNewsResult = List[Map[String, Any]]()
    val getTaxNewsQuery = f"""SELECT id, title FROM tax_news ORDER BY id DESC LIMIT $PAGE_SIZE%d"""
    db.withConnection{implicit conn =>
      getTaxNewsResult = SQL(
        getTaxNewsQuery.stripMargin).as(parser.*)
    }
    var getQnaResult = List[Map[String, Any]]()
    val getQnaQuery = f"""SELECT id, title FROM qna ORDER BY id DESC LIMIT $PAGE_SIZE%d"""
    db.withConnection{implicit conn =>
      getQnaResult = SQL(
        getQnaQuery.stripMargin).as(parser.*)
    }

    var result = Map[String, Any]()

    result += "fields" -> getFieldsResult
    result += "tax_news" -> getTaxNewsResult
    result += "qna" -> getQnaResult
    result
  }



}
