
package forms


import play.api.data._
import play.api.data.Forms._
import lib.model.TodoCategory.Color

case class EditCategoryData(
  id:    Int, 
  name:  String, 
  slug:  String, 
  color: Color
)

object EditCategoryForm {
  val editCategoryForm: Form[EditCategoryData] = Form(
    mapping(
      "id"    -> number, 
      "name"  -> nonEmptyText, 
      "slug"  -> nonEmptyText.verifying("error.numberOrEnglish", s => s.matches("^[a-zA-Z0-9]+$")), 
      "color" -> number.transform[Color](
        (colorId => Color.find(_.code == colorId).getOrElse(Color.RED)), 
        _.code
      )
    )(EditCategoryData.apply)(EditCategoryData.unapply)
  )
}