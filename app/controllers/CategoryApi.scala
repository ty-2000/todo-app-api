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
import json.writes.JsValueCategoryListItem

@Singleton
class CategoryApiController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  def index() = Action.async { implicit req => 
    TodoCategoryRepository.getAll().map(categorySeq => 
      Ok(Json.toJson(categorySeq.map(category => JsValueCategoryListItem(category))))
    )
  }
}

