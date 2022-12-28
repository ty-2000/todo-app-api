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
import play.api.i18n.I18nSupport
import play.api.mvc._

import model.ViewValueHome
import model.ViewValueTodo
import model.TodoWithCategory

import lib.persistence.onMySQL._
import lib.model.Todo
import lib.model.TodoCategory
import scala.concurrent.Await
import scala.concurrent.duration._

import play.api.data._
import play.api.data.Forms._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {

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

    for {
      todoSeq <- getTodoListFuture
      cateSeq <- getTodoCategoryFuture
    } yield {
      val vv = ViewValueTodo(
        title = "Todo一覧", 
        cssSrc = Seq("main.css"), 
        jsSrc  = Seq("main.js"), 
        todoList = todoSeq
      )
      val categoryNames = cateSeq.map(_.v.name)
      Ok(views.html.Todo(vv, categoryNames, addTodoForm))
    }
  }


  val addTodoForm = Form(
    mapping(
      "categoryId" -> number, 
      "title" -> text, 
      "body" -> text
    )(TodoData.apply)(TodoData.unapply)
  )

  def addTodo() = Action(parse.form(addTodoForm)).async { implicit req => 
    val todoData = req.body
    println(todoData.toString)
    val todoWithNoId: Todo#WithNoId = Todo.apply(
      categoryId = TodoCategory.Id(todoData.categoryId), 
      title      = todoData.title, 
      body       = todoData.body, 
      state      = lib.model.Todo.Status.TODO
    )
    val addTodoFuture = TodoRepository.add(todoWithNoId)
    addTodoFuture.map(id => 
      Redirect("/todo")  
    )
  }
}

case class TodoData(
  categoryId: Int, 
  title: String, 
  body: String
)
