
package forms


import play.api.data._
import play.api.data.Forms._

case class AddCategoryData(
  name: String, 
  slug: String, 
  color: Int
)

object AddCategoryForm {
  val addCategoryForm: Form[AddCategoryData] = Form(
    mapping(
      "name" -> nonEmptyText, 
      "slug" -> nonEmptyText.verifying("英数字のみ", s => s.matches("^[a-zA-Z0-9]+$")), 
      "color" -> number
    )(AddCategoryData.apply)(AddCategoryData.unapply)
  )
}