
package model

// Topページのviewvalue
case class ViewValueTodo(

  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],

  todoList: Seq[TodoWithCategory]

) extends ViewValueCommon
