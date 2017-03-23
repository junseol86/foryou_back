package models

/**
  * Created by Hyeonmin on 2017-03-23.
  */

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

class MonthlyJournalModel @Inject()(db: Database, common: Common) {

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def getSchedules(year: Int, month: Int): List[Map[String, Any]] = {
    var result = List[Map[String, Any]]()
    val getSchedulesQuery =
      f"""SELECT * FROM monthly_journal WHERE year = $year%d AND month = $month%d"""
    db.withConnection{implicit conn =>
      result = SQL(getSchedulesQuery.stripMargin).as(parser.*)
    }
    result
  }

}
