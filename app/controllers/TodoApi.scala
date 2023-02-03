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

import play.api.libs.json.Json
import json.writes.JsValueTodoListItem
import json.reads.JsValueCreateTodo

@Singleton
class TodoApiController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  def index() = Action.async { implicit req => 
    TodoRepository.getAll().map(todoSeq => 
      Ok(Json.toJson(todoSeq.map(todo => JsValueTodoListItem(todo))))
    )
  }

  def add() = Action(parse.json).async { implicit req => 
    req.body
      .validate[JsValueCreateTodo]
      .fold(
        errors => {
          Future.successful(Ok(Json.toJson("error")))
        }, 
        todoData => {
          val todoWithNoId: Todo#WithNoId = Todo.apply(
            categoryId = TodoCategory.Id(todoData.category_id), 
            title      = todoData.title, 
            body       = todoData.body, 
            state      = lib.model.Todo.Status.TODO
          )
          TodoRepository.add(todoWithNoId)
            .map(id => 
              Ok(Json.toJson(JsValueTodoListItem(todoWithNoId, id.toInt)))
            )
        }
      )
  }
}

