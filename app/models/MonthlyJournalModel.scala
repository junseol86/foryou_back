package models

/**
  * Created by Hyeonmin on 2017-03-23.
  */

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._
import models.UserModel

class MonthlyJournalModel @Inject()(db: Database, common: Common, userModel: UserModel) {

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

  def addSchedule(year: Int, month: Int, date: Int, content: String, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
//      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }

    println(authentication.toString)
    println(common.getDateFromToday(0))

    val account: Map[String, String] = authentication("account").asInstanceOf[Map[String, String]]
    var insertResult: Any = null;
    db.withConnection { implicit conn =>
      insertResult =
        SQL(
          """INSERT INTO monthly_journal (
            year, month, date, content, writer_id, created
            ) values (
            {year}, {month}, {date}, {content}, {writer_id}, {created}
            )
          """
        ).on('year -> year, 'month -> month, 'date -> date, 'content -> content, 'writer_id -> account("auth_token.user_id"), 'created -> common.getDateFromToday(0)).executeInsert()
    }
    insertResult match {
//        에러시 종료
      case Some(i: Long) =>
      case None => return common.returnSuccessResult(false)
    }

    val schedules = getASchedule(year, month, date)

    var toReturn = common.returnSuccessResult(true)
    toReturn += "schedules" -> schedules
    toReturn
  }

//  특정 날짜의 일정들 받아오기
  def getASchedule(year: Int, month: Int, date: Int): List[Map[String, Any]] = {
    val getSchedulesQuery =
      f"""SELECT * FROM monthly_journal WHERE year = $year%d AND month = $month%d AND date = $date%d"""
    db.withConnection { implicit conn =>
      return  SQL(getSchedulesQuery.stripMargin).as(parser.*)
    }
  }
}

