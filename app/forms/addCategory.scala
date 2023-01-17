
package forms


import play.api.data._
import play.api.data.Forms._
import lib.model.TodoCategory.Color

case class AddCategoryData(
  name: String, 
  slug: String, 
  color: Color
)

object AddCategoryForm {
  val addCategoryForm: Form[AddCategoryData] = Form(
    mapping(
      "name" -> nonEmptyText, 
      "slug" -> nonEmptyText.verifying("error.numberOrEnglish", s => s.matches("^[a-zA-Z0-9]+$")), 
      "color" -> number.transform[Color](
        (colorId => {
          println(colorId)
          Color.find(_.code == colorId).getOrElse(Color.RED)
        }), 
        _.code
      )
    )(AddCategoryData.apply)(AddCategoryData.unapply)
  )
}