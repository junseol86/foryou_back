package models

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._

/**
  * Created by Hyeonmin on 2017-02-28.
  */
class Test @Inject()(db: Database) {

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def testWork() = {
    var result = List[Map[String, Any]]()
    val query = "SELECT * FROM text"
    db.withConnection{implicit conn =>
      result = SQL(query.stripMargin).as(parser.*)
    }
    result
  }

}
