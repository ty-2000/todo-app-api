/**
 *
 * to do sample project
 *
 */

package controllers

// Futureを使えるようにimport
import scala.concurrent._

// おまじないだと思って無視してください
import scala.concurrent.ExecutionContext.Implicits.global


import scala.util.Success
import scala.util.Failure


import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import model.ViewValueTodo

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
    val getTodoListFuture: Future[Seq[(String, String, lib.model.Todo.Status, String, lib.model.TodoCategory.Color)]] = for {
        todoSeq <- TodoRepository.getAll()
        cateSeq <- TodoCategoryRepository.getAll()
    } yield {
      todoSeq.map(todo => {
          val category = cateSeq.find(cate => cate.v.id.get == TodoCategory.Id(todo.v.category_id)).get
          (todo.v.title, todo.v.body, todo.v.state, category.v.name, category.v.color)
        }
      )
    }
    val result = Await.ready(getTodoListFuture, Duration.Inf)

    val vv = ViewValueTodo(
      title   = "Todo一覧", 
      cssSrc  = Seq("main.css"), 
      jsSrc   = Seq("main.js"), 
      todoSeq = result.value.get match {
        case Success(v) => v
        case Failure(e) => Seq()
      }
    )
    println(vv)
    Ok(views.html.Todo(vv))
  }
}
