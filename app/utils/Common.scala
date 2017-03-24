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

  // 오늘로부터 #일이 지난 두의 시간을 반환.  0을 인자로 주면 현재 시각
  def getDateFromToday(daysAfter: Int):String = {
    val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    val currentDate = new Date();
    val calendar = Calendar.getInstance()
    calendar.setTime(currentDate)
    calendar.add(Calendar.DATE, daysAfter)
    dateFormat.format(calendar.getTime)
  }

  // 특정 작업의 성공, 실패 여부 반환.  성공시 받아온 결과 등을 Map에 추가하여 클라이언트로 전송하면 된다
  def returnSuccessResult(result: Boolean): Map[String, Any] = {
    var failResult = Map[String, Any]()
    failResult += "success" -> result
    failResult
  }

}
