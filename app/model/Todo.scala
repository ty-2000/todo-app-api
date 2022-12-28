
package model

case class TodoWithCategory(
  todo: lib.model.Todo, 
  category: lib.model.TodoCategory
)

// Topページのviewvalue
case class ViewValueTodo(

  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],

  todoList: Seq[TodoWithCategory]

) extends ViewValueCommon
