/**
 *
 * to do sample project
 *
 */

package controllers

// Futureを使えるようにimport
import scala.concurrent.Future

// おまじないだと思って無視してください
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject._
import play.api.mvc._

import model.ViewValueHome

import lib.persistence.onMySQL._
import lib.model.Todo
import lib.model.TodoCategory
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

  def getTodoList() = Action { implicit req => 
    val getTodoListFuture: Future[Seq[(String, String, lib.model.Todo.Status, String)]] = for {
        todoSeq <- TodoRepository.getAll()
        cateSeq <- TodoCategoryRepository.getAll()
    } yield {
      todoSeq.map(todo =>
        (todo.v.title, todo.v.body, todo.v.state, cateSeq.find(cate => cate.v.id.get == TodoCategory.Id(todo.v.category_id)).get.v.name)
      )
    }
    val result = Await.ready(getTodoListFuture, Duration.Inf)
    Ok(result.toString)
  }
}
