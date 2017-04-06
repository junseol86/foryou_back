package controllers.tax_info

import javax.inject._

import models._
import org.json4s._
import org.json4s.native.{Json, Serialization}
import play.api.mvc._
import utils._

/**
  * Created by Hyeonmin on 2017-04-03.
  */
class TaxNewsController @Inject()(taxNewsModel: TaxNewsModel, common: Common) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getTaxNews(page: Int, search: String) = Action {
    var result = Map[String, Any]()
    result += "data" -> taxNewsModel.getTaxNews(page, search)
    Ok(Json(formats).write(result))
  }

  def writeTaxNews() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> taxNewsModel.writeTaxNews(
      fp.get("title"), fp.get("tags"), fp.get("content"),
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

  def modifyTaxNews() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> taxNewsModel.modifyTaxNews(
      fp.get("id").toInt, fp.get("title"), fp.get("tags"), fp.get("content"),
      fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }

  def deleteTaxNews() = Action { request =>
    def fp = new common.FromPost(request)
    var result = Map[String, Any]()
    result += "data" -> taxNewsModel.deleteTaxNews(
      fp.get("id").toInt, fp.get("selector"), fp.get("validator")
    )
    Ok(Json(formats).write(result));
  }
}
