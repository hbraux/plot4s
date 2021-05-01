package fr.braux.myscala

import fr.braux.myscala.Plotdef._

import java.awt.Color
import scala.util.{Failure, Success, Try}

abstract class Renderer {
  // texture type
  type Texture

  // properties
  def background: Color
  def width: Int
  def height: Int

  // actions
  def refresh(): Unit
  def nextEvent(): PlotEvent
  def close(): Unit
  def getRaw: Array[Byte]

  // low level APIs
  def point(p: Point): Unit
  def points(ps: Iterable[Point], joined: Boolean): Unit
  def line(from: Point, to: Point): Unit
  def rect(p: Point, w: Int, h: Int): Unit
  def pixels(ps: Iterable[(Point, Color)]): Unit

  def useColor(color: Color): Unit
  def useLine(width: Float): Unit

  // texture handling
  def load(filePath: String): Texture
  def useTexture(t: Texture)

  // set parameters
  def useParams(params: PlotParams): Unit = {
    params.eval(PlotColor, (v: Color) => useColor(v))
    params.eval(PlotLineWidth, (v: Float) => useLine(v))
  }

  // 2D high level APis
  private var tileSize = 1

  def useTiles(rows: Int, columns: Int): Unit = tileSize = width/columns min height/rows

  def tile(row: Int, column: Int, c: Color): Unit = {
    if (c != background) {
      useColor(c)
      val p = Point(column * tileSize, row * tileSize)
      if (tileSize == 1) point(p) else rect(p, tileSize, tileSize)
    }
  }
}

abstract class RendererFactory {
  def defaultSize: Int
  def defaultBackground: Color
  def apply(title: String, width: Int, height: Int, background: Color): Renderer
}

// The RendererFactory is loaded dynamically to avoid compile dependencies like lwjgl
object RendererFactory {
  def apply(name: String) : RendererFactory = {
    // RendererFactory shall be a Scala Object name
    val className = if (name contains '.') name else this.getClass.getPackage.getName + "." + name + "Renderer$"
    require(className endsWith "$", s"Not an Object: $name")
    Try(Class.forName(className)) match {
      case Success(cl) => cl.getField("MODULE$").get(cl).asInstanceOf[RendererFactory]
      case Failure(e) => throw new IllegalArgumentException(s"cannot load Class $className", e)
    }
  }
}
