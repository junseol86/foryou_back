package models

import com.google.inject.Inject

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

/**
  * Created by Hyeonmin on 2017-04-10.
  */
class FormsModel @Inject()(db: Database, common: Common, security: Security, sql: Sql, userModel: UserModel) {

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

  def getForms(): List[Map[String, Any]] = {
    var forms = List[Map[String, Any]]()
    val query = "SELECT * FROM forms ORDER BY id DESC"
    db.withConnection{implicit conn =>
      forms = SQL(
        query.stripMargin).as(parser.*)
    }
    forms
  }

  def uploadForm(title: String, file_url: String, selector: String, validator: String): Map[String, Any] = {
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
          """INSERT INTO forms (
            title, file_url, writer_id, uploaded
            ) values (
            {title}, {file_url}, {writer_id}, {uploaded}
            )
          """
        ).on('title -> title, 'file_url -> file_url, 'writer_id -> account("auth_token.user_id"), 'uploaded -> common.getDateFromToday(0)).executeInsert()
    }
    insertResult match {
      //        에러시 종료
      case Some(i: Long) => return common.returnSuccessResult(true)
      case None => return common.returnSuccessResult(false)
    }
  }

  def deleteForm(id: Int, selector: String, validator: String): Map[String, Any] = {
    val authentication = userModel.authenticate(selector, validator)
    if (authentication("success") == false) {
      //      유저확인 실패시 종료
      return common.returnSuccessResult(false)
    }
    common.returnSuccessResult(
      sql.deleteAContent("forms", id) == 1
    )
  }

}
