package json.writes

import play.api.libs.json.{Json, Writes}
import lib.model.TodoCategory
import lib.model.TodoCategory.Color


case class JsValueColor(
  code: Short, 
  name: String
)
object JsValueColor {
  implicit val writes: Writes[JsValueColor] = Json.writes[JsValueColor]

  def apply(color: Color): JsValueColor = JsValueColor(code=color.code, name=color.name)
}

case class JsValueCategoryListItem(
  id:      Long, 
  name:    String, 
  slug:    String, 
  color:   JsValueColor
)
object JsValueCategoryListItem {
  implicit val writes: Writes[JsValueCategoryListItem] = Json.writes[JsValueCategoryListItem]

  def apply(category: TodoCategory.EmbeddedId): JsValueCategoryListItem =
    JsValueCategoryListItem(
      id       = category.id.toLong,
      name     = category.v.name, 
      slug     = category.v.slug, 
      color    = JsValueColor(category.v.color)
    )
}

