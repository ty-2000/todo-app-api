package json.writes

import play.api.libs.json.{Json, Writes}
import lib.model.Todo
import lib.model.Todo.Status


case class JsValueStatus(
  code: Short, 
  name: String
)
object JsValueStatus {
  implicit val writes: Writes[JsValueStatus] = Json.writes[JsValueStatus]

  def apply(state: Status): JsValueStatus = JsValueStatus(code=state.code, name=state.name)
}

case class JsValueTodoListItem(
  id:         Long,
  title:      String,
  body:       String,
  state:      JsValueStatus,
  category_id: Long
)
object JsValueTodoListItem {
  implicit val writes: Writes[JsValueTodoListItem] = Json.writes[JsValueTodoListItem]

  def apply(todo: Todo.EmbeddedId): JsValueTodoListItem =
    JsValueTodoListItem(
      id       = todo.id.toLong,
      title    = todo.v.title,
      body     = todo.v.body,
      state    = JsValueStatus(todo.v.state),
      category_id = todo.v.categoryId.toLong
    )
}

