/**
 *
 * to do sample project
 *
 */

package controllers

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure


import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import model.ViewValueTodo
import model.TodoWithCategory

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

  def getTodoList() = Action.async { implicit req => 
    val getTodoFuture = TodoRepository.getAll()
    val getTodoCategoryFuture = TodoCategoryRepository.getAll()

    val getTodoListFuture: Future[Seq[TodoWithCategory]] = for {
        todoSeq <- getTodoFuture
        cateSeq <- getTodoCategoryFuture
    } yield {
      todoSeq.map(todo => {
          val category = cateSeq.find(
            _.v.id.getOrElse(TodoCategory.Id(-1)) == todo.v.categoryId
          ).getOrElse(
            TodoCategory.apply(name="ERROR", slug="ERROR", color=TodoCategory.Color.RED)
          )
          TodoWithCategory(
            todo = todo.v, 
            category = category.v
          )
        }
      )
    }
    getTodoListFuture.map(todoList => {
        val vv = ViewValueTodo(
          title   = "Todo一覧", 
          cssSrc  = Seq("main.css"), 
          jsSrc   = Seq("main.js"), 
          todoList = todoList
        )
        Ok(views.html.Todo(vv))
      }
    )
  }
}
