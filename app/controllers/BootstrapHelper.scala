package controllers

import views.html.helper.{twitterBootstrapFieldConstructor, FieldConstructor}

object BootstrapHelper {

  implicit val fields = FieldConstructor(twitterBootstrapFieldConstructor.f)

}
