package models

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

/**
  * Created by Hyeonmin on 2017-04-03.
  */
class FieldsModel @Inject()(db: Database, common: Common, sql: Sql, userModel: UserModel) {

  val PAGE_SIZE = 30

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def getFields(submenu: String, page: Int, search: String):Map[String, Any] = {
    val searchWord = search.replace("@", "")
    val condition =
      f"""
         WHERE submenu = "$submenu"
         AND (title LIKE "%%$searchWord%s%%"
         OR content LIKE "%%$searchWord%s%%"
         OR tags LIKE "%%$searchWord%s%%")
       """.stripMargin
    sql.getList("fields", condition, page, PAGE_SIZE)
  }

  def writeFields(submenu: String, title: String, tags: String, content: String, selector: String, validator: String): Map[String, Any] = {
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
          """INSERT INTO fields (
            submenu, title, tags, content, writer_id, created, modified
            ) values (
            {submenu}, {title}, {tags}, {content}, {writer_id}, {created}, {modified}
            )
          """
        ).on('submenu -> submenu, 'title -> title, 'tags -> tags, 'content -> content, 'writer_id -> account("auth_token.user_id"), 'created -> common.getDateFromToday(0), 'modified -> common.getDateFromToday(0)).executeInsert()
    }
    insertResult match {
      //        에러시 종료
      case Some(i: Long) => return common.returnSuccessResult(true)
      case None => return common.returnSuccessResult(false)
    }
  }

}
