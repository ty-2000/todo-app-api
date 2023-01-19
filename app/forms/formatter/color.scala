
package forms.formatter

import play.api.data._
import play.api.data.format.{ Formats, Formatter }
import lib.model.TodoCategory.Color

trait ColorFormatter {
  implicit def colorFormatter: Formatter[Color] = new Formatter[Color] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Color] = {
      Formats.stringFormat.bind(key, data).right.map(
        colorIdStr => Color.find(_.code == colorIdStr.toInt)
      ).flatMap {
        case Some(color) => Right(color)
        case None        => Left(Seq(FormError(key, "error.invalid", Nil)))
      }
    }

    override def unbind(key: String, value: Color): Map[String, String] =
      Map(key -> value.code.toString) 
  }
}
