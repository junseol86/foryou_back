package controllers.online_consulting

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models._

/**
  * Created by Hyeonmin on 2017-03-29.
  */
class FaqController @Inject()(faqModel: FaqModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getFaqs(page: Int) = Action {
    var result = Map[String, Any]()
    result += "data" -> faqModel.getFaqs(page)
    Ok(Json(formats).write(result))
  }

  def addFaq() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> faqModel.addFaq(
      fp.get("topic"), fp.get("question"), fp.get("answer"),
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

  def modifyFaq() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> faqModel.modifyFaq(
      fp.get("id").toInt,
      fp.get("topic"), fp.get("question"), fp.get("answer"),
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

  def deleteFaq() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> faqModel.deleteFaq(
      fp.get("id").toInt, fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

}
