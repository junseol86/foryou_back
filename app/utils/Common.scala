package utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar

import play.api.mvc._

/**
  * Created by Hyeonmin on 2017-03-20.
  */
class Common {
  class FromPost(request: Request[AnyContent]) {
    def get(str: String) = {
      request.body.asFormUrlEncoded.get(str).head
    }
  }

  def getDateFromToday(daysAfter: Int):String = {
    val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    val currentDate = new Date();
    val calendar = Calendar.getInstance()
    calendar.setTime(currentDate)
    calendar.add(Calendar.DATE, daysAfter)
    dateFormat.format(calendar.getTime)
  }

}
