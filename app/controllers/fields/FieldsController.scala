package controllers.fields

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models._

/**
  * Created by Hyeonmin on 2017-04-03.
  */
class FieldsController @Inject()(fieldsModel: FieldsModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getFields(submenu: String, page: Int, search: String) = Action {
    var result = Map[String, Any]()
    result += "data" -> fieldsModel.getFields(submenu, page, search)
    Ok(Json(formats).write(result))
  }

  def writeFields() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> fieldsModel.writeFields(
      fp.get("submenu"), fp.get("title"), fp.get("tags"), fp.get("content"),
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

}
