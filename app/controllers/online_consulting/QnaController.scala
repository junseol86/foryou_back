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

  def writeQuestion() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> qnaModel.writeQuestion(
      fp.get("asker"), fp.get("email"), fp.get("password"),
      fp.get("title"), fp.get("question")
    )
    Ok(Json(formats).write(result));
  }

}
