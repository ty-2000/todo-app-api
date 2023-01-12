
package forms


import play.api.data._
import play.api.data.Forms._

case class AddCategoryData(
  name: String, 
  slug: String, 
  color: Int
)

object AddCategoryForm {
  val addCategoryForm = Form(
    mapping(
      "name" -> nonEmptyText, 
      "slug" -> nonEmptyText, 
      "color" -> number
    )(AddCategoryData.apply)(AddCategoryData.unapply)
  )
}