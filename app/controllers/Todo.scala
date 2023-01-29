/**
 *
 * to do sample project
 *
 */

package controllers

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._

import model.ViewValueHome
import model.TodoWithCategory

import lib.persistence.onMySQL._
import lib.model.Todo
import lib.model.TodoCategory

import play.api.data._
import play.api.data.Forms._

import forms.AddTodoForm.addTodoForm
import forms.EditTodoForm.editTodoForm

@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {

  val errorTodoCategory = TodoCategory.apply(id=Some(TodoCategory.Id(-1)), name="ERROR", slug="ERROR", color=TodoCategory.Color.RED).toEmbeddedId

  def getTodoList() = Action.async { implicit req => 
    val getAllTodoFuture = TodoRepository.getAll()
    val getAllTodoCategoryFuture = TodoCategoryRepository.getAll()

    val getAllTodoWithCategoryFuture: Future[Seq[TodoWithCategory]] = for {
        todoSeq     <- getAllTodoFuture
        categorySeq <- getAllTodoCategoryFuture
    } yield {
      todoSeq.map(todo => {
          val category = categorySeq.find(
            _.id == todo.v.categoryId
          ).getOrElse(errorTodoCategory)
          TodoWithCategory(
            todo = todo, 
            category = category
          )
        }
      )
    }

    val vv = ViewValueHome(
      title = "Todo一覧", 
      cssSrc = Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )

    for {
      todoWithCategorySeq <- getAllTodoWithCategoryFuture
      categorySeq         <- getAllTodoCategoryFuture
    } yield {
      Ok(views.html.todo.Todo(vv, todoWithCategorySeq, categorySeq, addTodoForm))
    }
  }

  def getTodo(id: Int) = Action.async { implicit req => 

    val vv = ViewValueHome(
      title  = "Edit",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )
    val getTodoFuture = TodoRepository.get(Todo.Id(id))
    val getAllTodoCategoryFuture = TodoCategoryRepository.getAll()
    
    for {
      todoOpt <- getTodoFuture
      categorySeq <- getAllTodoCategoryFuture
    } yield {
      todoOpt match {
        case Some(todo) => {
          val category = categorySeq.find(_.id == todo.v.categoryId
          ).getOrElse(errorTodoCategory)
          val todoWithCategory = TodoWithCategory(
            todo = todo, 
            category = category
          )
          val filledEditTodoForm = editTodoForm.fill(
            forms.EditTodoData(
              id = todo.id.toInt, 
              title = todoWithCategory.todo.v.title, 
              body = todoWithCategory.todo.v.body, 
              status = todoWithCategory.todo.v.state.code, 
              categoryId = todoWithCategory.todo.v.categoryId.toInt, 
            )
          )
          Ok( views.html.todo.Edit( vv, todoWithCategory, categorySeq, filledEditTodoForm ) )
        }
        case None => {
          Redirect(routes.TodoController.getTodoList)
        }
      }
    }
  }

  def addTodo() = Action(parse.form(addTodoForm)).async { implicit req => 
    val todoData = req.body
    val todoWithNoId: Todo#WithNoId = Todo.apply(
      categoryId = TodoCategory.Id(todoData.categoryId), 
      title      = todoData.title, 
      body       = todoData.body, 
      state      = lib.model.Todo.Status.TODO
    )
    val addTodoFuture = TodoRepository.add(todoWithNoId)
    addTodoFuture.map(id => 
      Redirect(routes.TodoController.getTodoList)
    )
  }



  def editTodo() = Action(parse.form(editTodoForm)).async { implicit req => 
    val todoData = req.body
    val getNewTodoOptFuture = TodoRepository.get(Todo.Id(todoData.id)).map{
      _.map(
        _.map(
          _.copy(
            title = todoData.title, 
            body = todoData.body, 
            state = Todo.Status.find(_.code == todoData.status).getOrElse(Todo.Status.TODO), 
            categoryId = TodoCategory.Id(todoData.categoryId)
          )
        )
      )
    }

    for {
      newTodoOpt <- getNewTodoOptFuture
      updatedTodoOption <- newTodoOpt match {
        case Some(newTodo) => TodoRepository.update(newTodo)
        case None          => Future.successful(None)
      }
    } yield {
      Redirect(routes.TodoController.getTodoList)
    }
  }

  def deleteTodo(id: Int) = Action.async {implicit req => 
    TodoRepository.remove(Todo.Id(id)).map{ _ => 
      Redirect(routes.TodoController.getTodoList)
    }
  }
}

