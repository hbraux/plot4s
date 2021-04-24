package fr.braux.myscala

import fr.braux.myscala.Plotlib._

/**
 * This is the main implementation which is agnostic of Graphic API
 */
abstract class Plotter {

  val factory: RendererFactory
  lazy val rdr: Renderer = factory.instance

  def plotGraph(provider: PointProvider, params: Seq[PlotParam]): Unit = {
    rdr.lines(provider.getPoints(rdr.display))
    rdr.refresh()
    waitForExit()
  }

  private def waitForExit(): Unit = {
    var waiting = true
    while (waiting) {
      rdr.nextKeyEvent() match {
        case Some(k) if k == PlotKeyEscape => waiting = false
        case _ =>
      }
    }
    rdr.close()
  }
}

object Plotter {
  class FunctionPointProvider(f: Double => Double) extends PointProvider {
    // generate 1 point per horizontal pixel
    override def getPoints(display: Display): Seq[Point] = (0 to display.width).map { n =>
      val x: Float = display.pmin.x + (display.pmax.x - display.pmin.x) * n / display.width
      Point(x, f(x).toFloat)
    }
  }
}
