
package model
import lib.persistence.onMySQL._

case class TodoWithCategory(
  todo: TodoRepository.EntityEmbeddedId, 
  category: TodoCategoryRepository.EntityEmbeddedId
)
