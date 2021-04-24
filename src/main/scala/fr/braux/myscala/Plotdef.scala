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
   * Display provides both information about the Window being displayed and the 2D or 3D space that
   * is rendered by this window (pmin / pmax are left-bottom-front / top-right-back coordinates)
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
  import PlotValueType._
  sealed class PlotConst(val valueType: PlotValueType)

  // Supported APIs
  case object PlotApiOpenGl extends PlotConst(BoolValue)
  case object PlotApiConsole extends PlotConst(BoolValue)

  // Window Parameters
  case object PlotWindowTitle extends PlotConst(StringValue)
  case object PlotWindowHeight extends PlotConst(IntValue)
  case object PlotWindowWidth extends PlotConst(IntValue)
  case object PlotWindowColor extends PlotConst(ColorValue)
  case object PlotLineColor extends PlotConst(ColorValue)
  case object PlotLineWidth extends PlotConst(FloatValue)

  // Keys
  case object PlotKeyEscape extends PlotConst(NoValue)
  case object PlotKeySpace extends PlotConst(NoValue)

  /**
   * A Parameter is a tuple Constant / Value; the value will be matched against the expected value type
   */
  type PlotParam = (PlotConst, Any)

  case class PlotSettings(settings: Map[PlotConst, Any]) {
    def get[A](c: PlotConst, orElse: A): A = {
      settings.get(c) match {
        case Some(v: A) => v
        case _ => orElse
      }
    }
  }

  object PlotSettings {
    def apply(params: Seq[PlotParam] = Seq.empty): PlotSettings = {
      params.forall { p =>
        (p._1.valueType, p._2) match {
          case (StringValue, x: String) => true
          case (IntValue, x: Int) => true
          case (FloatValue, x: Float) => true
          case (ColorValue, x: Color) => true
          case (BoolValue, x: Boolean) => true
          case (ConstValue, x: PlotConst) => true
          case (_, NoValue) => throw new IllegalArgumentException(s"${p._1} is not a parameter")
          case _ => throw new IllegalArgumentException(s"Expecting a type ${p._2} for ${p._1}")
        }
      }
      PlotSettings(params.toMap)
    }
  }
}

object PlotValueType extends Enumeration {
  type PlotValueType = Value
  val NoValue, StringValue, IntValue, FloatValue, BoolValue, ConstValue, ColorValue = Value
}