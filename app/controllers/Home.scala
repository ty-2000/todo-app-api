/**
 *
 * to do sample project
 *
 */

package controllers

import javax.inject._
import play.api.mvc._

import model.ViewValueHome

import lib.persistence.onMySQL._
import lib.model.Todo
import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit req =>
    val vv = ViewValueHome(
      title  = "Home",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )
    Ok(views.html.Home(vv))
  }

  def getTodo(id: Option[Int]) = Action { implicit req => 
    val result = 
      if(id.isDefined) {
        val f = TodoRepository.get(Todo.Id(id.get))
        Await.ready(f, Duration.Inf)
      } else {
        val f = TodoRepository.getAllYouNeed()
        Await.ready(f, Duration.Inf)
      }
    Ok(result.toString)
  }
}
