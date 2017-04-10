package controllers.online_consulting

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models._

/**
  * Created by Hyeonmin on 2017-04-06.
  */
class QnaController @Inject()(qnaModel: QnaModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getQnas(page: Int, search: String) = Action {
    var result = Map[String, Any]()
    result += "data" -> qnaModel.getQnas(page, search)
    Ok(Json(formats).write(result))
  }

  def readQuestion() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> qnaModel.readQuestion(
      fp.get("id").toInt, fp.get("selector"), fp.get("validator"),
      fp.get("password")
    )
    Ok(Json(formats).write(result));
  }

  def writeQuestion() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> qnaModel.writeQuestion(
      fp.get("asker"), fp.get("email"), fp.get("password"),
      fp.get("title"), fp.get("question")
    )
    Ok(Json(formats).write(result));
  }

  def answerQuestion() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> qnaModel.answerQuestion(
      fp.get("id").toInt, fp.get("answer"), fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

  def deleteQuestion() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> qnaModel.deleteQuestion(
      fp.get("id").toInt, fp.get("selector"), fp.get("validator"), fp.get("password")
    )
    Ok(Json(formats).write(result));
  }

}
