package fr.braux.myscala

import fr.braux.myscala.PlotValueType.{NoValue, PlotValueType}
import fr.braux.myscala.Plotter.FunctionPointProvider


/**
 * Plotdef provides all definitions and constants
 */
object Plotdef  {

  /**
   * A Point is a set of 3D coordinates (floats avoid conversions for OpenGL)
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
    def zoom(factor: Float): Boolean
  }

  /**
   * A Plot Handler
   */
  trait PlotHandler {
    def plot: () => Unit
    def callback: PlotEvent => Boolean
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
   * Events
   */
  sealed class PlotEvent
  case object PlotEventNone extends PlotEvent
  case object PlotEventTimer extends PlotEvent
  case object PlotEventEscape extends PlotEvent
  case object PlotEventSpace extends PlotEvent
  case object PlotEventLeft extends PlotEvent
  case object PlotEventRight extends PlotEvent
  case object PlotEventUp extends PlotEvent
  case object PlotEventDown extends PlotEvent
  case object PlotEventPageUp extends PlotEvent
  case object PlotEventPageDown extends PlotEvent

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

  // Shapes
  case object PlotColor extends PlotConst(ColorValue)
  case object PlotLineWidth extends PlotConst(FloatValue)

  // Misc
  case object PlotTimer extends PlotConst(IntValue) // in milliseconds
  case object PlotScale extends PlotConst(FloatValue)

  /**
   * A Parameter is a tuple Constant / Value; the value will be matched against the expected value type
   */
  type PlotParam = (PlotConst, Any)

  trait Plottable {
    def plot(params: PlotParam*): Unit
  }

  case class PlotSettings(settings: Map[PlotConst, Any]) {
    def get[A](c: PlotConst, orElse: A): A = {
      settings.get(c) match {
        case Some(v: A) => v
        case _ => orElse
      }
    }
    def eval[A](c: PlotConst, f: A => Unit): Unit = settings.get(c) match {
      case Some(v: A) => f(v)
      case _ =>
    }
  }

  object PlotSettings {
    def apply(params: Seq[PlotParam] = Seq.empty): PlotSettings = PlotSettings(
      params.map { param =>
        (param._1.valueType, param._2) match {
          case (StringValue, v: String) => param
          case (IntValue, v: Int) => param
          case (FloatValue, v: Float) => param
          case (FloatValue, v: Double) => (param._1, v.toFloat)
          case (ColorValue, v: Color) => param
          case (BoolValue, v: Boolean) => param
          case (ConstValue, v: PlotConst) => param
          case (_, NoValue) => throw new IllegalArgumentException(s"${param._1} is not a parameter")
          case _ => throw new IllegalArgumentException(s"Expecting a type ${param._1.valueType.toString.replace("Value","")} for ${param._1}")
        }
      }.toMap)
  }
}

object PlotValueType extends Enumeration {
  type PlotValueType = Value
  val NoValue, StringValue, IntValue, FloatValue, BoolValue, ConstValue, ColorValue = Value
}