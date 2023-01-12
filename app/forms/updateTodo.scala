
package forms


import play.api.data._
import play.api.data.Forms._

case class EditTodoData(
  id:         Int, 
  title:      String, 
  body:       String, 
  status:     Int, 
  categoryId: Int, 
)

object EditTodoForm {
  val editTodoForm = Form(
    mapping(
      "id" -> number, 
      "title" -> text, 
      "body" -> text, 
      "status" -> number, 
      "categoryId" -> number
    )(EditTodoData.apply)(EditTodoData.unapply)
  )
}
