package controllers

import javax.inject._

import models.Test
import play.api.mvc._
import org.json4s._
import org.json4s.native.Serialization._
import org.json4s.native.{Json, Serialization}

/**
  * Created by Hyeonmin on 2017-02-28.
  */

class TestController @Inject()(testMdl: Test) extends Controller {

  implicit val formats = Serialization.formats(NoTypeHints)

  def test = Action { request =>
    var result = List[Map[String, Any]]()
    result = testMdl.testWork()
    Ok(Json(formats).write(result))
  }

}
