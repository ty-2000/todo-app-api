/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.Todo
import lib.model.TodoCategory
import slick.jdbc.JdbcProfile

// TodoRepository: TodoTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class TodoRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Todo.Id, Todo, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
    * Get Todo Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
  }

  def getAll(): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(TodoTable, "slave") { _
      .result
  }



  def getAllYouNeed(): Future[Seq[(Long, String, String, lib.model.Todo.Status)]] = 
    RunDBAction(TodoTable, "slave") { _
      .map(todo => (todo.categoryId, todo.title, todo.body, todo.state))
      .result
  }

  def getAllYouNeedExceptStatus(): Future[Seq[(Long, String, String)]] = 
    RunDBAction(TodoTable, "slave") { _
      .map(todo => (todo.categoryId, todo.title, todo.body))
      .result
    }

  /**
    * Add Todo Data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(TodoTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update Todo Data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /**
   * Delete Todo Data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}

case class TodoCategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[TodoCategory.Id, TodoCategory, P]
  with db.SlickResourceProvider[P] {

  import api._

  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoCategoryTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
  }

  def getAll(): Future[Seq[EntityEmbeddedId]] = 
    RunDBAction(TodoCategoryTable, "slave") { _
      .result
    }

  /**
    * Add Todo Data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(TodoCategoryTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update Todo Data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoCategoryTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /**
   * Delete Todo Data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoCategoryTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}