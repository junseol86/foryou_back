package controllers.account

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models.UserModel

/**
  * Created by Hyeonmin on 2017-03-20.
  */
class UserController @Inject()(userModel: UserModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def login = Action { request =>
    def fp = new common.FromPost(request)
    val id = fp.get("user_id")
    val pw = fp.get("password")

    var result = Map[String, Any]()
    result += "data" -> userModel.login(id, pw)

    Ok(Json(formats).write(result))
  }

  def autoLogin = Action { request =>
    def fp = new common.FromPost(request)
    val selector = fp.get("selector")
    val validator = fp.get("validator")

    var result = Map[String, Any]()
    result += "data" -> userModel.autoLogin(selector, validator)

    Ok(Json(formats).write(result))
  }

  def authenticate = Action { request =>
    def fp = new common.FromPost(request)
    val selector = fp.get("selector")
    val validator = fp.get("validator")

    var result = Map[String, Any]()
    result += "data" -> userModel.authenticate(selector, validator)

    Ok(Json(formats).write(result))
  }

  def changePassword() = Action { request =>
    def fp = new common.FromPost(request)
    val password = fp.get("password")
    val new_password = fp.get("new_password")
    val selector = fp.get("selector")
    val validator = fp.get("validator")

    var result = Map[String, Any]()
    result += "data" -> userModel.changePassword(password, new_password, selector, validator)
    Ok(Json(formats).write(result))
  }

  def addAdmin() = Action { request =>
    def fp = new common.FromPost(request)
    val user_id = fp.get("user_id")
    val user_pw = fp.get("user_pw").trim
    val user_name = fp.get("user_name")
    val user_position = fp.get("user_position")
    val selector = fp.get("selector")
    val validator = fp.get("validator")

    var result = Map[String, Any]()
    result += "data" -> userModel.addAdmin(user_id, user_pw, user_name, user_position, selector, validator)
    Ok(Json(formats).write(result))
  }
}
