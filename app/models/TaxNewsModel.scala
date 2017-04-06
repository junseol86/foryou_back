package models

import com.google.inject.Inject

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

/**
  * Created by Hyeonmin on 2017-04-05.
  */
class TaxNewsModel @Inject()(db: Database, common: Common, sql: Sql, userModel: UserModel) {

  val PAGE_SIZE = 30

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }


  def getTaxNews(page: Int, search: String):Map[String, Any] = {
    val searchWord = search.replace("@", "")
    val condition =
      f"""
         WHERE title LIKE "%%$searchWord%s%%"
         OR content LIKE "%%$searchWord%s%%"
         OR tags LIKE "%%$searchWord%s%%"
       """.stripMargin
    sql.getList("tax_news", condition, page, PAGE_SIZE)
  }

  def writeTaxNews(title: String, tags: String, content: String, selector: String, validator: String): Map[String, Any] = {
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
          """INSERT INTO tax_news (
            title, tags, content, writer_id, created, modified
            ) values (
            {title}, {tags}, {content}, {writer_id}, {created}, {modified}
            )
          """
        ).on('title -> title, 'tags -> tags, 'content -> content, 'writer_id -> account("auth_token.user_id"), 'created -> common.getDateFromToday(0), 'modified -> common.getDateFromToday(0)).executeInsert()
    }
    insertResult match {
      //        에러시 종료
      case Some(i: Long) => return common.returnSuccessResult(true)
      case None => return common.returnSuccessResult(false)
    }
  }

  def modifyTaxNews(id: Int, title: String, tags: String, content: String, selector: String, validator: String): Map[String, Any] = {
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
          """UPDATE tax_news SET
             title = {title}, tags = {tags}, content = {content}, writer_id = {writer_id}, modified = {modified}
             WHERE id = {id}
          """
        ).on(
          'title -> title, 'tags -> tags, 'content -> content, 'writer_id -> account("auth_token.user_id"), 'modified ->  common.getDateFromToday(0), 'id -> id
        ).executeUpdate()
    }
    common.returnSuccessResult(modifyResult == 1)
  }

  def deleteTaxNews(id: Int, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
      //      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }
    common.returnSuccessResult(
      sql.deleteAContent("tax_news", id) == 1
    )
  }

}
