
package model

case class TodoWithCategory(
  title:    String, 
  body:     String, 
  status:   lib.model.Todo.Status, 
  category: String, 
  color:    lib.model.TodoCategory.Color
)

// Topページのviewvalue
case class ViewValueTodo(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],

  todoSeq: Seq[(String, String, lib.model.Todo.Status, String, lib.model.TodoCategory.Color)]

) extends ViewValueCommon