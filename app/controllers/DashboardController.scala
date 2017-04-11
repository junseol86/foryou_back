package controllers

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models._

/**
  * Created by Hyeonmin on 2017-04-11.
  */
class DashboardController @Inject()(dashboardModel: DashboardModel) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getArticles() = Action {
    var result = Map[String, Any]()
    result += "data" -> dashboardModel.getArticles
    Ok(Json(formats).write(result))
  }
}
