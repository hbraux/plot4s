package fr.braux.myscala

import fr.braux.myscala.Plotlib._

/**
 * This is the main implementation which is agnostic of Graphic API
 */
abstract class Plotter {

  val rdr: Renderer

  def plotGraph(provider: PointProvider, params: Seq[PlotParam]): Unit = {
    rdr.init()
    rdr.lines(provider)
    rdr.show()
  }
}

object Plotter {
  class MathFunctionPointProvider(f: Double => Double) extends PointProvider {
    // generate 1 point per horizontal pixel
    override def getPoints(display: Display): Seq[Point] = (0 to display.width).map { n =>
      val x: Float = display.pmin.x + (display.pmax.x - display.pmin.x) * n / display.width
      Point(x, f(x).toFloat)
    }
  }
}
