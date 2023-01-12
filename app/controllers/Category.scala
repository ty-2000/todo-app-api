/**
 *
 * to do sample project
 *
 */

package controllers

import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

import model.ViewValueHome

import lib.persistence.onMySQL._ // repository
import lib.model.TodoCategory

import forms.AddCategoryForm.addCategoryForm


@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {

  def getList() = Action.async { implicit req =>
    val vv = ViewValueHome(
      title = "カテゴリ一覧", 
      cssSrc = Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )
    TodoCategoryRepository.getAll().map{ categorySeq => 
      Ok(views.html.Category(vv, categorySeq))
    }
  }

  def add() = Action(parse.form(addCategoryForm)).async { implicit req => 
    val categoryData = req.body
    val categoryWithNoId: TodoCategory#WithNoId = TodoCategory.apply(
      name       = categoryData.name, 
      slug       = categoryData.slug, 
      color      = TodoCategory.Color.find(_.code == categoryData.color).getOrElse(TodoCategory.Color.RED)
    )
    val addCategoryFuture = TodoCategoryRepository.add(categoryWithNoId)
    addCategoryFuture.map(id => 
      Redirect("/todo")  
    )
  }
}

