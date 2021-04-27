package fr.braux.myscala


/**
 * Plotdef provides all definitions and constants
 */
object Plotdef  {

  trait Plottable  {
    def plot(params: (PlotConst, Any)*): Unit = Plotter(params).plot(this)
    def render: Plotter => Boolean
    def handlers: Map[PlotEvent, () => Boolean] = Map.empty
    def isScalable: Boolean
  }

  trait Playable extends Plottable {
    def next(): () => Boolean
    def play(f: () => Boolean, params: (PlotConst, Any)*): Unit = Plotter(params).plot(this)
  }

  trait PlottableRealFunction extends Function1[Double,Double] with Plottable  {
    override val render: Plotter => Boolean = _.plotGraph(this)
    override val isScalable = true
  }

  trait PlottableMatrix[T] extends Function2[Int,Int,T] with Plottable {
    def rows: Int
    def columns: Int
    override val render: Plotter => Boolean = _.plotTiles[T](this)
    override val isScalable = false
  }

  /**
   * A Point is a set of 3D coordinates (floats avoid conversions for OpenGL)
   */
  case class Point(x: Float, y: Float, z: Float = 0f)


  /**
   * A Color is defined by its R-G-B values between 0.0 and 1.0
   */
  case class Color(red: Float = 0, green: Float = 0, blue: Float = 0)
  val Black: Color = Color()
  val Red: Color = Color(red = 1)
  val Green: Color = Color(green = 1)
  val Blue: Color = Color(blue = 1)
  val Yellow: Color = Color(red = 1, green = 1)
  val Purple: Color = Color(red = 1, blue = 1)
  val Cyan: Color = Color(green = 1, blue = 1)
  val White: Color = Color(red = 1, green = 1, blue = 1)
  // orders by RGB bits
  val Colors: Array[Color] = Array(Black, Red, Green, Yellow, Blue, Purple, Cyan, White)

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


  // Window Parameters
  case object PlotWindowTitle extends PlotConst(StringValue)
  case object PlotWindowHeight extends PlotConst(IntValue)
  case object PlotWindowWidth extends PlotConst(IntValue)
  case object PlotWindowColor extends PlotConst(ColorValue)

  // Shapes
  case object PlotColor extends PlotConst(ColorValue)
  case object PlotBackground extends PlotConst(ColorValue)
  case object PlotLineWidth extends PlotConst(FloatValue)

  // Misc
  case object PlotTimer extends PlotConst(IntValue) // in milliseconds
  case object PlotScale extends PlotConst(FloatValue)
  case object PlotConsole extends PlotConst(BoolValue)

}

object PlotValueType extends Enumeration {
  type PlotValueType = Value
  val NoValue, StringValue, IntValue, FloatValue, BoolValue, ConstValue, ColorValue = Value
}