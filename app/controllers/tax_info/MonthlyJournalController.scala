package controllers.tax_info

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

import models.MonthlyJournalModel

/**
  * Created by Hyeonmin on 2017-03-23.
  */
class MonthlyJournalController @Inject()(monthlyJournalModel: MonthlyJournalModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getSchedules(year: Int, month: Int) = Action {
    var result = Map[String, Any]()
    result += "data" -> monthlyJournalModel.getSchedules(year, month)
    Ok(Json(formats).write(result))
  }

  def addASchedule = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" ->
      monthlyJournalModel.addSchedule(fp.get("year").toInt, fp.get("month").toInt, fp.get("date").toInt, fp.get("content"), fp.get("selector"), fp.get("validator"))
    Ok(Json(formats).write(result))
  }

  def deleteASchedule = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" ->
      monthlyJournalModel.deleteASchedule(fp.get("year").toInt, fp.get("month").toInt, fp.get("date").toInt, fp.get("id").toInt, fp.get("selector"), fp.get("validator"))
    Ok(Json(formats).write(result))
  }

}