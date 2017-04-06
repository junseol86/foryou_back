package models

import com.google.inject.Inject

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

/**
  * Created by Hyeonmin on 2017-04-06.
  */
class QnaModel @Inject()(db: Database, common: Common, security: Security, sql: Sql, userModel: UserModel) {

  val PAGE_SIZE = 30

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def getQnas(page: Int, search: String):Map[String, Any] = {
    val searchWord = search.replace("@", "")
    val condition =
      f"""
         WHERE asker LIKE "%%$searchWord%s%%"
         OR title LIKE "%%$searchWord%s%%"
       """.stripMargin
    sql.getList("tax_news", condition, page, PAGE_SIZE)
  }

  def writeQuestion(asker: String, email: String, password: String, title: String, question: String): Map[String, Any] = {
    var insertResult: Any = null
    db.withConnection { implicit conn =>
      insertResult =
        SQL(
          """INSERT INTO qna (
            asker, email, password, title, question, asked
            ) values (
            {asker}, {email}, {password}, {title}, {question}, {asked}
            )
          """
        ).on('asker -> asker, 'email -> email, 'passeord -> security.sha256Hashing(password), 'title -> title, 'question -> question, 'asked -> common.getDateFromToday(0)).executeInsert()
    }
    insertResult match {
      //        에러시 종료
      case Some(i: Long) => return common.returnSuccessResult(true)
      case None => return common.returnSuccessResult(false)
    }
  }

}
