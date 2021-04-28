package fr.braux.myscala

import fr.braux.myscala.Plotdef._

trait Renderer {
  // texture type
  type Texture

  // properties
  def background: Color
  def width: Int
  def height: Int
  def minPoint: Point
  def maxPoint: Point

  // actions
  def refresh(): Unit
  def nextEvent(): PlotEvent
  def close(): Unit
  def asBinary(): Array[Byte]

  // low level APIs
  def point(a: Point): Unit
  def line(a: Point, b: Point): Unit
  def triangle(a: Point, b: Point, c: Point): Unit
  def quad(a: Point, b: Point, c: Point, d: Point): Unit

  def lines(points: Iterable[Point]): Unit
  def useColor(c: Color): Unit
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
  private var tileSize = 1f

  def useTiles(rows: Int, columns: Int): Unit = {
    tileSize = (maxPoint.x - minPoint.x)/columns min (maxPoint.y - minPoint.y)/rows
  }

  def tile(row: Int, column: Int, c: Color): Unit = {
    if (c != background) {
      useColor(c)
      val p = Point(minPoint.x + column * tileSize, minPoint.y + row * tileSize)
      quad(p, p.copy(x = p.x + tileSize), p.copy(x = p.x + tileSize, y = p.y + tileSize), p.copy(y = p.y + tileSize))
    }
  }

  // return the X coordinate of each horizontal pixel
  def xRange: Seq[Float] = (0 until width).map { i => minPoint.x + (maxPoint.x - minPoint.x) * i / width }
  
}
