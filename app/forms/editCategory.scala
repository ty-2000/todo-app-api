
package forms


import play.api.data._
import play.api.data.Forms._

case class EditCategoryData(
  id:    Int, 
  name:  String, 
  slug:  String, 
  color: Int
)

object EditCategoryForm {
  val editCategoryForm: Form[EditCategoryData] = Form(
    mapping(
      "id"    -> number, 
      "name"  -> nonEmptyText, 
      "slug"  -> nonEmptyText.verifying("英数字のみ", s => s.matches("^[a-zA-Z0-9]+$")), 
      "color" -> number
    )(EditCategoryData.apply)(EditCategoryData.unapply)
  )
}