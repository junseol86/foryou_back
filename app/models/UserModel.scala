package models

/**
  * Created by Hyeonmin on 2017-03-20.
  */

import javax.inject._

import anorm.{RowParser, SqlParser, _}
import play.api.db._
import utils._

class UserModel @Inject()(db: Database, security: Security, common: Common) {

  val LOGIN_SUCCESS = 1
  val WRONG_ID_PW = 0
  val LOGIN_ERROR = 2

  val parser: RowParser[Map[String, Any]] =
    SqlParser.folder(Map.empty[String, Any]) { (map, value, meta) =>
      Right(map + (meta.column.qualified -> value))
    }

//  아이디와 패스워드를 입력한 최초 로그인
  def login(user_id:String, password:String): Map[String, Any] = {
    var result = List[Map[String, Any]]()
    val checkIdPwQuery =
      f"""SELECT * FROM user WHERE user_id = '$user_id%s'"""
    db.withConnection{implicit conn =>
      result = SQL(checkIdPwQuery.stripMargin).as(parser.*)
    }

    if (result.length == 0 || result(0)("user.user_pw") != security.sha256Hashing(password + result(0)("user.user_salt"))) {
//      ID가 없거나 salt, hash된 password가 일치하지 않을 때
      var resultMap = Map[String, Any]()
      resultMap += "loginResult" -> WRONG_ID_PW
      return resultMap
    }

    setAuthToken(user_id)
  }

//  쿠키의 selector와 validator로 실행하는 자동 로그인
  def autoLogin(selector: String, validator: String): Map[String, Any] = {
    var result = List[Map[String, Any]]()
    val checkIdPwQuery =
      f"""SELECT * FROM auth_token WHERE selector = '$selector%s'"""
    db.withConnection{implicit conn =>
      result = SQL(checkIdPwQuery.stripMargin).as(parser.*)
    }
    if (result.length == 0 || result(0)("auth_token.token") != security.sha256Hashing(validator) || result(0)("auth_token.expires").toString < common.getDateFromToday(0)) {
      var resultMap = Map[String, Any]()
      resultMap += "loginResult" -> WRONG_ID_PW
      return resultMap
    }

    setAuthToken(result(0)("auth_token.user_id").toString)
  }

  def createUniqueSelector: String = {
    //      현존하지 않는 selector 생성
    val selector = security.createSelector()
    var result = List[Map[String, Any]]()
    val query =
      f"""SELECT count(*) count FROM auth_token WHERE selector = '$selector%s'"""
    db.withConnection{implicit conn =>
      result = SQL(query.stripMargin).as(parser.*)
    }
    if (result(0)(".count").toString.toInt > 0)
      createUniqueSelector
    else
      selector
  }

  def setAuthToken(user_id: String): Map[String, Any] = {
    //    해당 아이디로 등록된 토큰이 있다면 삭제
    val deleteAuthTokenQuery =
      f"""DELETE FROM auth_token WHERE user_id = '$user_id%s'"""
    db.withConnection{implicit conn =>
      SQL(deleteAuthTokenQuery.stripMargin).executeUpdate()
    }
    //    selector와 validator 생성
    val selector = createUniqueSelector
    val validator = security.createValidator()
    val token = security.sha256Hashing(validator)
    val expires = common.getDateFromToday(7)

    var createAuthTokenResult: Any = null
    db.withConnection { implicit conn =>
      createAuthTokenResult = SQL(
        """
          INSERT INTO auth_token (
          selector, token, user_id, expires
          ) values (
          {selector}, {token}, {user_id}, {expires}
          )
      """
      )
        .on(
          'selector -> selector, 'token -> token, 'user_id -> user_id, 'expires -> expires
        ).executeInsert()
    }

    var resultMap = Map[String, Any]()
    createAuthTokenResult match {
          case Some(i: Long) => {
            resultMap += "loginResult" -> LOGIN_SUCCESS
            resultMap += "selector" -> selector
            resultMap += "validator" -> validator
            resultMap += "userId" -> user_id
          }
          case None => resultMap += "loginResult" -> LOGIN_ERROR
        }
    resultMap
  }

}

