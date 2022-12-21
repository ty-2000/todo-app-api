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

  def getTodo(id: Option[Int]) = Action { implicit req => 
    val result = 
      if(id.isDefined) {
        val f = for {
          todo <- TodoRepository.get(Todo.Id(id.get))
          cate <- TodoCategoryRepository.get(TodoCategory.Id(todo.get.v.category_id))
        } yield {
          (todo.get.v.title, todo.get.v.body, todo.get.v.state, cate.get.v.name)
        }
        Await.ready(f, Duration.Inf)

      } else {
        val f = for {
            todoSeq <- TodoRepository.getAll()
            cateSeq <- TodoCategoryRepository.getAll()
        } yield {
          todoSeq.map(todo =>
            (todo.v.title, todo.v.body, todo.v.state, cateSeq.find(cate => cate.v.id.get == TodoCategory.Id(todo.v.category_id)).get.v.name)
          )
        }
        Await.ready(f, Duration.Inf)
      }
    Ok(result.toString)
  }

  def getTodoOnly() = Action { implicit req => 
    val f = TodoRepository.getAll()
    val result = Await.ready(f, Duration.Inf)
    Ok(result.toString)
  }

  def getTodoCategory() = Action { implicit req => 
    val f = TodoCategoryRepository.getAll()
    val result = Await.ready(f, Duration.Inf)
    Ok(result.toString)
  }
}
