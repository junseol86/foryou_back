package controllers

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models._

/**
  * Created by Hyeonmin on 2017-04-10.
  */
class FormsController @Inject()(formsModel: FormsModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getForms() = Action {
    var result = Map[String, Any]()
    var forms = List[Map[String, Any]]()
    forms = formsModel.getForms()
    result += "data" -> forms
    Ok(Json(formats).write(result))
  }

  def uploadForm() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> formsModel.uploadForm(
      fp.get("title"), fp.get("file_url"),
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result))
  }

  def deleteForm() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> formsModel.deleteForm(
      fp.get("id").toInt,
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result))
  }


}
