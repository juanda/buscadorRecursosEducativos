package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.{MongoDriver, QueryOpts}

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONInteger, BSONString, BSONDocument}
import reactivemongo.core.commands.Count

import scala.concurrent.Future
import scala.util.{Failure, Success}

import play.modules.reactivemongo.MongoController

object Recursos extends Controller with MongoController{

  def collection: BSONCollection = db.collection("Recurso")

  def countDocuments(query: BSONDocument) = collection.db.command(Count(collection.name, Option(query)))

  def index = Action {

    Ok(views.html.recursos.index(formBuscador))
  }

  def procesa = Action { implicit request =>

  	formBuscador.bindFromRequest.fold(
  		formWithErrors => {
    BadRequest(views.html.recursos.index(formWithErrors))
    },
    buscador => {
    	val text = buscador.text
    	Redirect(routes.Recursos.recursos(text, 1))
    }
    )
  }

  def recursos(text:String, page:Int) = Action.async{
    val pageLength = Play.current.configuration.getInt("pageLength")
    val query = BSONDocument("$text" -> BSONDocument("$search" -> text))
    val options = QueryOpts((page-1)*pageLength.getOrElse(20), pageLength.getOrElse(20))

//    val filter = BSONDocument(
//      "titulo" -> 1,
//      "url_recurso" -> 1,
//      "imagen" -> 1
//    )

    val futureCount: Future[Int] = countDocuments(query)

    var count = 0
    futureCount.onSuccess{
      case c => count = c
    }

    val cursor = collection.find(query).options(options).sort(BSONDocument("titulo" -> 1)).cursor[BSONDocument]

    val futureRecursosList: Future[List[BSONDocument]] = cursor.collect[List]()

    futureRecursosList.map { recursos => {
      Ok(views.html.recursos.recursos(recursos)(count, page, pageLength.get)(text))
      }
    }
  }

  def ficha(id: String) = Action.async{ implicit request =>
    val query = BSONDocument("id" -> id)

    val cursor = collection.find(query).cursor[BSONDocument]

    val futureRecurso: Future[List[BSONDocument]] = cursor.collect[List]()

    futureRecurso.map { recursos => Ok(views.html.recursos.ficha(recursos))}
  }

  val formBuscador = Form(
	mapping(
		"text" -> nonEmptyText(minLength=2, maxLength=255)
		)(Buscador.apply)(Buscador.unapply)

	)
}

case class Buscador(text: String)


