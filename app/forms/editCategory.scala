
package forms


import play.api.data._
import play.api.data.Forms._

import forms.formatter.ColorFormatter
import lib.model.TodoCategory.Color

case class EditCategoryData(
  id:    Int, 
  name:  String, 
  slug:  String, 
  color: Color
)

object EditCategoryForm extends ColorFormatter {
  val editCategoryForm: Form[EditCategoryData] = Form(
    mapping(
      "id"    -> number, 
      "name"  -> nonEmptyText, 
      "slug"  -> nonEmptyText.verifying("error.numberOrEnglish", s => s.matches("^[a-zA-Z0-9]+$")), 
      "color" -> Forms.of[Color]
    )(EditCategoryData.apply)(EditCategoryData.unapply)
  )
}