
package forms


import play.api.data._
import play.api.data.Forms._

case class AddTodoData(
  categoryId: Int, 
  title:      String, 
  body:       String
)

object AddTodoForm {
  val addTodoForm = Form(
    mapping(
      "categoryId" -> number, 
      "title" -> text, 
      "body" -> text
    )(AddTodoData.apply)(AddTodoData.unapply)
  )
}
