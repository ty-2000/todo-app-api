
package forms


import play.api.data._
import play.api.data.Forms._

import forms.formatter.ColorFormatter
import lib.model.TodoCategory.Color

case class AddCategoryData(
  name: String, 
  slug: String, 
  color: Color
)

object AddCategoryForm extends ColorFormatter {
  val addCategoryForm: Form[AddCategoryData] = Form(
    mapping(
      "name" -> nonEmptyText, 
      "slug" -> nonEmptyText.verifying("error.numberOrEnglish", s => s.matches("^[a-zA-Z0-9]+$")), 
      "color" -> Forms.of[Color]
    )(AddCategoryData.apply)(AddCategoryData.unapply)
  )
}