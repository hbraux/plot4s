package fr.braux.myscala

import java.awt.Color

/**
 * Plotdef provides all definitions and constants
 */
object Plotdef  {

  trait Plottable  {
    def plot(params: (PlotConst, Any)*): Renderer = Plotter(params).plot(this)
    val render: Plotter => Unit
    val handlers: Map[PlotEvent, () => Boolean] = Map.empty
    val isScalable: Boolean
  }

  trait Playable extends Plottable {
    def playNext(): Boolean
    def play(f: () => Boolean, params: (PlotConst, Any)*): Unit = Plotter(params).plot(this)
  }

  trait PlottableDoubleFunction extends Function1[Double,Double] with Plottable  {
    override val render: Plotter => Unit = _.graph(this)
    override val isScalable = true
  }

  trait PlottableScalarFunction extends Function2[Double, Double, Double] with Plottable  {
    override val render: Plotter => Unit = _.image(this)
    override val isScalable = true
  }


  trait PlottableMatrix[T] extends Function2[Int,Int,T] with Plottable {
    def rows: Int
    def columns: Int
    override val render: Plotter => Unit = _.tiles[T](this)
    override val isScalable = false
  }

  /**
   * Point: 2D with (0,0) at top left corner
   * Vertex: 3D with default ranges from -1.0 to 1.0 (OpenGL)
   */
  case class Point(x: Int, y: Int)
  case class Vertex(x: Float, y: Float, z: Float = 0f)

  val BasicColors: Array[Color] = Array(Color.white, Color.black, Color.red, Color.green, Color.blue, Color.yellow, Color.magenta, Color.cyan)
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
  abstract class PlotConst(val valueType: PlotValueType)


  // Global Parameters
  case object PlotTitle extends PlotConst(StringValue)
  case object PlotHeight extends PlotConst(IntValue)
  case object PlotWidth extends PlotConst(IntValue)
  case object PlotBackground extends PlotConst(ColorValue)

  // Shapes
  case object PlotColor extends PlotConst(ColorValue)
  case object PlotLineWidth extends PlotConst(FloatValue)

  // Misc
  case object PlotTimer extends PlotConst(IntValue) // milliseconds
  case object PlotScale extends PlotConst(FloatValue)
  case object PlotToRaw extends PlotConst(BoolValue)

  case object PlotRenderer extends PlotConst(StringValue)

}

object PlotValueType extends Enumeration {
  type PlotValueType = Value
  val NoValue, StringValue, IntValue, FloatValue, BoolValue, ConstValue, ColorValue = Value
}