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
import forms.EditCategoryForm.editCategoryForm

import play.api.data._
import play.api.data.Forms._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {

  def getCategoryList() = Action.async { implicit req =>
    val vv = ViewValueHome(
      title = "カテゴリ一覧", 
      cssSrc = Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )
    TodoCategoryRepository.getAll().map{ categorySeq => 
      Ok(views.html.category.Category(vv, categorySeq))
    }
  }

  def getCategory(id: Int) = Action.async { implicit req => 
    val vv = ViewValueHome(
      title  = "Category 編集", 
      cssSrc =  Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )

    TodoCategoryRepository.get(TodoCategory.Id(id)).map{ 
      _ match {
        case None               => Redirect(routes.CategoryController.getCategoryList)
        case Some(todoCategory) => {
          val filledEditTodoForm = editCategoryForm.fill(
            forms.EditCategoryData(
              id    = todoCategory.id.toInt, 
              name  = todoCategory.v.name, 
              slug  = todoCategory.v.slug, 
              color = todoCategory.v.color
            )
          )
          Ok(views.html.category.Edit(vv, filledEditTodoForm))
        }
      }
    }
  }

  def addCategoryHome() = Action { implicit req =>
    val vv = ViewValueHome(
      title = "Category追加", 
      cssSrc = Seq("main.css"), 
      jsSrc  = Seq("main.js"), 
    )
    Ok(views.html.category.Add(vv, addCategoryForm))
  }

  def addCategory() = Action.async { implicit request => 
    addCategoryForm.bindFromRequest.fold(
      formWithErrors => {
        val vv = ViewValueHome(
          title = "Category追加", 
          cssSrc = Seq("main.css"), 
          jsSrc  = Seq("main.js"), 
        )
        Future.successful(BadRequest(views.html.category.Add(vv, formWithErrors)))
      }, 
      categoryData => {
        val categoryWithNoId: TodoCategory#WithNoId = TodoCategory.apply(
          name       = categoryData.name, 
          slug       = categoryData.slug, 
          color      = categoryData.color
        )
        TodoCategoryRepository.add(categoryWithNoId).map(_ => 
          Redirect(routes.CategoryController.getCategoryList)  
        )
      }
    )
  }

  def editCategory() = Action.async {implicit req => 
    editCategoryForm.bindFromRequest.fold(
      formWithErrors => {
        val vv = ViewValueHome(
          title = "Category編集", 
          cssSrc = Seq("main.css"), 
          jsSrc  = Seq("main.js"), 
        )
        Future.successful(BadRequest(views.html.category.Edit(vv, formWithErrors)))
      }, 
      categoryData => {
        for {
          newTodoCategoryOpt <- TodoCategoryRepository.get(TodoCategory.Id(categoryData.id)).map{
            _.map(
              _.map(_.copy(
                  name = categoryData.name, 
                  slug = categoryData.slug, 
                  color = categoryData.color
                )
              )
            )
          }
          res <- newTodoCategoryOpt match {
            case Some(newTodoCategory) => TodoCategoryRepository.update(newTodoCategory).map(_ => Redirect(routes.CategoryController.getCategoryList))
            case None                  => Future.successful(None).map(_ => Ok(views.html.category.ErrorEdit()))
          }
        }yield{
            res
        }
      }
    )
  }
  
  def deleteCategory(id: Int) = Action.async { implicit req => 
    val categoryId = TodoCategory.Id(id)
    val deletedTodoSeqFuture = for {
      oldTodoSeq <- TodoRepository.getByCategoryId(categoryId)
      deletedTodoSeq <- Future.sequence(oldTodoSeq.map(oldTodo => TodoRepository.remove(oldTodo.id)))
    } yield {
      deletedTodoSeq
    }
    val deletedTodoCategoryFuture = TodoCategoryRepository.remove(categoryId)
    for {
      deletedTodoSeq <- deletedTodoSeqFuture
      deletedTodoCategory <- deletedTodoCategoryFuture
    } yield {
      Redirect(routes.CategoryController.getCategoryList)
    }
  }
}

