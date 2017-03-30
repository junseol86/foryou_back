package models

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

/**
  * Created by Hyeonmin on 2017-03-29.
  */
class FaqModel @Inject()(db: Database, common: Common, sql: Sql, userModel: UserModel) {

  val PAGE_SIZE = 20

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def getFaqs(page: Int):Map[String, Any] = {
    sql.getList("faq", "", page, PAGE_SIZE)
  }

  def addFaq(topic: String, question: String, answer: String, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
      //      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }

    val account: Map[String, String] = authentication("account").asInstanceOf[Map[String, String]]
    var insertResult: Any = null
    db.withConnection { implicit conn =>
      insertResult =
        SQL(
          """INSERT INTO faq (
            topic, question, answer, writer_id
            ) values (
            {topic}, {question}, {answer}, {writer_id}
            )
          """
        ).on('topic -> topic, 'question -> question, 'answer -> answer, 'writer_id -> account("auth_token.user_id")).executeInsert()
    }
    insertResult match {
      //        에러시 종료
      case Some(i: Long) => return common.returnSuccessResult(true)
      case None => return common.returnSuccessResult(false)
    }
  }

  def modifyFaq(id: Int, topic: String, question: String, answer: String, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
      //      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }
    val account: Map[String, String] = authentication("account").asInstanceOf[Map[String, String]]
    var modifyResult: Int = 0
    db.withConnection { implicit conn =>
      modifyResult =
        SQL(
          """UPDATE faq SET
             topic = {topic}, question = {question}, answer = {answer}, writer_id = {writer_id}
             WHERE id = {id}
          """
        ).on(
          'id -> id, 'topic -> topic, 'question -> question, 'answer -> answer, 'writer_id ->  account("auth_token.user_id")
        ).executeUpdate()
    }
    common.returnSuccessResult(modifyResult == 1)
  }

  def deleteFaq(id: Int, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
      //      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }
    common.returnSuccessResult(
      sql.deleteAContent("faq", id) == 1
    )
  }
}
