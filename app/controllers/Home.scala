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
import model.TodoWithCategory

import lib.persistence.onMySQL._
import lib.model.Todo
import lib.model.TodoCategory

import play.api.data._
import play.api.data.Forms._

import forms.AddTodoForm.addTodoForm
import forms.EditTodoForm.editTodoForm

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
}

