package fr.braux.myscala

abstract class Plot {
  import Plot._

  val rdr: Renderer

  def plotGraph(f: Double => Double): Unit = {
    rdr.init("graph")
    val (Point(xmin, _, _), Point(xmax, _, _)) = rdr.corners
    val points = (0 to 1000).map{ i =>
      val x = xmin + (xmax- xmin) * i/1000
      Point(x, f(x).toFloat)
    }
    rdr.lines(points)
    rdr.show()
  }

}

object Plot {

  case class Point(x: Float, y: Float, z: Float = 0f)

  case class Color(red: Float = 0, green: Float = 0, blue: Float = 0)
  val Black: Color = Color()
  val Red: Color = Color(red = 1)
  val Green: Color = Color(green = 1)
  val Blue: Color = Color(blue = 1)
  val White: Color = Color(red = 1, green = 1, blue = 1)

  object Setting extends Enumeration {
    val Width, Height = Value
  }

  trait Renderer {
    type Texture
    def init(title: String, params: Map[Setting.Value, Int] = Map.empty): Unit
    def corners: (Point, Point)
    def color(c: Color): Unit
    def point(a: Point): Unit
    def line(a: Point, b: Point): Unit
    def triangle(a: Point, b: Point, c: Point): Unit
    def quad(a: Point, b: Point, c: Point, d: Point): Unit
    def lines(points: Iterable[Point]): Unit
    def close(): Unit
    def load(filePath: String): Texture
    def use(t: Texture)
    def show(wait: Boolean = true): Unit
  }

}


