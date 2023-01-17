/**
 *
 * to do sample project
 *
 */

package controllers

import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._

import model.ViewValueHome

import lib.persistence.onMySQL._ // repository
import lib.model.TodoCategory

import forms.AddCategoryForm.addCategoryForm

import play.api.data._
import play.api.data.Forms._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {

  def getList() = Action.async { implicit req =>
    val vv = ViewValueHome(
      title = "カテゴリ一覧", 
      cssSrc = Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )
    TodoCategoryRepository.getAll().map{ categorySeq => 
      Ok(views.html.category.Category(vv, categorySeq))
    }
  }

  def addHome() = Action { implicit req =>
    val vv = ViewValueHome(
      title = "Category追加", 
      cssSrc = Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )
    Ok(views.html.category.add(vv, addCategoryForm))
  }

  def add() = Action.async { implicit request => 
    addCategoryForm.bindFromRequest.fold(
      formWithErrors => {
        val vv = ViewValueHome(
          title = "Category追加", 
          cssSrc = Seq("main.css"), 
          jsSrc  = Seq("main.js"), 
        )
        Future.successful(BadRequest(views.html.category.add(vv, formWithErrors)))
      }, 
      categoryData => {
        val categoryWithNoId: TodoCategory#WithNoId = TodoCategory.apply(
          name       = categoryData.name, 
          slug       = categoryData.slug, 
          color      = TodoCategory.Color.find(_.code == categoryData.color).getOrElse(TodoCategory.Color.RED)
        )
        TodoCategoryRepository.add(categoryWithNoId).map(_ => 
          Redirect(routes.CategoryController.getList)  
        )
      }
    )
  }
}

