package fr.braux.myscala


/**
 * Plotlib provides all definitions and constants
 * It shall be be imported with ._
 */
object Plotlib  {

  /**
   * A Point is a set of 3D coordinates
   * Using Floats to avoid convertions for OpenGL
   */
  case class Point(x: Float, y: Float, z: Float = 0f)

  /**
   * Display provides both information about the Window being displayed and the 2D/3D space that
   * is rendered within this window (pmin / pmax are left-bottom-front / top-right-back coordinates)
   */
  case class Display(width: Int, height: Int, pmin: Point, pmax: Point) {
    def zoom(f: Float): Display = {
      val (wx, wy, wz) = (pmax.x - pmin.x, pmax.y - pmin.y, pmax.z - pmin.z)
      val c = Point(pmin.x + wx/2, pmin.y + wy/2, pmin.z + wz/2)
      Display(width, height, Point(c.x - wx*f, c.y - wy*f, c.z - wz*f), Point(c.x + wx*f, c.y - wy*f, c.z - wz*f))
    }
  }

  /**
   * A PointProvider is used by Renderer to get new points when zooming or moving the veiwed space
   */
  trait PointProvider {
    def getPoints(display: Display) : Seq[Point]
  }

  /**
   * A Color is defined by its R-G-B values between 0.0 and 1.0
   */
  case class Color(red: Float = 0, green: Float = 0, blue: Float = 0)
  val Black: Color = Color()
  val Red: Color = Color(red = 1)
  val Green: Color = Color(green = 1)
  val Blue: Color = Color(blue = 1)
  val White: Color = Color(red = 1, green = 1, blue = 1)

  /**
   *  Constants, which their associated value types when
   */
  import ValueType._
  sealed class PlotConst(vt: ValueType)

  // Supported APIs
  case object PlotApiOpenGl extends PlotConst(NoValue)
  case object PlotApiConsole extends PlotConst(NoValue)

  // Window Parameters
  case object PlotWindowTitle extends PlotConst(StringValue)
  case object PlotWindowHeight extends PlotConst(IntValue)
  case object PlotWindowWidth extends PlotConst(IntValue)
  case object PlotWindowColor extends PlotConst(ColorValue)
  case object PlotLineColor extends PlotConst(ColorValue)
  case object PlotLineWidth extends PlotConst(FloatValue)

  /**
   * A Parameter is a tuple Constant / Value; the value will be matched against the expected value type
   */
  type PlotParam = (PlotConst, Any)


}

object ValueType extends Enumeration {
  type ValueType = Value
  val NoValue, StringValue, IntValue, FloatValue, ColorValue = Value
}