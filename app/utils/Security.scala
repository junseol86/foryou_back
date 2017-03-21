package utils

import java.security.SecureRandom

/**
  * Created by Hyeonmin on 2017-03-17.
  */
class Security {

  val letters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

  def sha256Hashing(pw:String):String = {
    val m = java.security.MessageDigest.getInstance("SHA-256")
    val b = pw.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }


  def createRandomString(length:Int): String = {
    def addLetter(i: Int, s: String): String = {
      if (i > 0) {
        addLetter(i - 1, s + letters.charAt((new SecureRandom().nextDouble() * letters.length).toInt))
      }
      else s
    }
    addLetter(length, "")
  }

  def createSalt(): String = createRandomString(20)
  def createSelector(): String = createRandomString(12)
  def createValidator(): String = createSelector()

}
