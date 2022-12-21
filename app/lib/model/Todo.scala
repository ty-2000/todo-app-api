/**
  * This is a sample of Todo Application.
  * 
  */

package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// Todoを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Todo._
case class Todo(
  id:           Option[Id],
  categoryId:   Long, 
  title:        String,
  body:         String,
  state:        Status,
  updatedAt:    LocalDateTime = NOW,
  createdAt:    LocalDateTime = NOW
) extends EntityModel[Id]

import  TodoCategory._
case class TodoCategory(
  id:             Option[Id], 
  name:           String, 
  slug:           String, 
  color:          Color, 
  updatedAt:      LocalDateTime = NOW, 
  createdAt:      LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Todo {

  val  Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId [Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Status(val code: Short, val name: String) extends EnumStatus
  object Status extends EnumStatus.Of[Status] {
    case object TODO        extends Status(code = 0,   name = "TODO")
    case object IN_PROGRESS extends Status(code = 1, name = "進行中")
    case object DONE        extends Status(code = 2, name = "完了")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(categoryId: Int, title: String, body: String, state: Status): WithNoId = {
    new Entity.WithNoId(
      new Todo(
        id          = None,
        categoryId  = categoryId, 
        title       = title, 
        body        = body, 
        state       = state
      )
    )
  }
}

object TodoCategory {
  val  Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId [Id, TodoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, TodoCategory]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Color(val code: Short, val name: String) extends EnumStatus
  object Color extends EnumStatus.Of[Color] {
    case object RED        extends Color(code = 1, name = "red")
    case object BLUE       extends Color(code = 2, name = "blue")
    case object GREEN      extends Color(code = 3, name = "green")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(name: String, slug: String, color: Color): WithNoId = {
    new Entity.WithNoId(
      new TodoCategory(
        id          = None,
        name        = name, 
        slug        = slug, 
        color       = color, 
      )
    )
  }
}
