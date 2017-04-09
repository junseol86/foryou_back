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
    sql.getList("qna", condition, page, PAGE_SIZE)
  }

  def readQuestion(id: Int, selector: String, validator: String, password: String): Map[String, Any] = {
    if (selector != "" && validator != "") {
//      관리자일 때
      val authentication = userModel.authenticate(selector, validator)
      if (authentication("success") == false) {
        //      유저확인 실패시 종료
        return common.returnSuccessResult(false)
      }
    } else {
      //      질문을 올린 사용자일 때
      val passwordHash = security.sha256Hashing(password)
      var check = List[Map[String, Any]]()
      val checkQuery = f"""SELECT password FROM qna WHERE id = $id%d"""
      db.withConnection { implicit conn =>
        check = SQL(
          checkQuery.stripMargin).as(parser.*)
      }
      if (check.length == 0 || check(0)("qna.password") != passwordHash) {
        return common.returnSuccessResult(false)
      }
    }

    val readQuery = f"""SELECT id, asker, email, title, question, answer, asked, answered, views FROM qna WHERE id = $id%d"""
    var read = List[Map[String, Any]]()
    db.withConnection { implicit conn =>
      read = SQL(
        readQuery.stripMargin).as(parser.*)
    }

    var result = common.returnSuccessResult(true)
    result += "detail" -> read(0)
    result

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
        ).on('asker -> asker, 'email -> email, 'password -> security.sha256Hashing(password), 'title -> title, 'question -> question, 'asked -> common.getDateFromToday(0)).executeInsert()
    }
    insertResult match {
      //        에러시 종료
      case Some(i: Long) => return common.returnSuccessResult(true)
      case None => return common.returnSuccessResult(false)
    }
  }

  def answerQuestion(id: Int, answer: String, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
      //      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }

    val account: Map[String, String] = authentication("account").asInstanceOf[Map[String, String]]
    var modifyResult: Int = 0

    db.withConnection { implicit conn =>
      modifyResult =
        if (answer != "null")
          SQL(
            """UPDATE qna SET
               answer = {answer}, answerer = {answerer}, answered = {answered}
               WHERE id = {id}
            """
          ).on(
            'answer -> answer, 'answerer -> account("auth_token.user_id"), 'answered ->  common.getDateFromToday(0), 'id -> id
          ).executeUpdate()
      else
          SQL(
            """UPDATE qna SET
               answer = NULL, answerer = {answerer}, answered = {answered}
               WHERE id = {id}
            """
          ).on(
            'answerer -> account("auth_token.user_id"), 'answered ->  common.getDateFromToday(0), 'id -> id
          ).executeUpdate()
    }
    common.returnSuccessResult(modifyResult == 1)
  }

}
