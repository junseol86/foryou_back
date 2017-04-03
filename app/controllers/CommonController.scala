package controllers

import javax.inject._
import play.api.mvc._
import org.json4s.native.{Json, Serialization}
import org.json4s._
import utils._

/**
  * Created by Hyeonmin on 2017-04-03.
  */
class CommonController @Inject()(sql: Sql) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def getDetail(table: String, id: Int) = Action {
    var result = Map[String, Any]()
    result += "data" ->
      sql.getDetail(table, id)
    Ok(Json(formats).write(result))
  }
}
