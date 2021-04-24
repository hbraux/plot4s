package fr.braux.myscala

import fr.braux.myscala.Plotlib._

/**
 * This is the main implementation which is agnostic of Graphic API
 */
abstract class Plotter {

  // the Renderer is instanciated lazily from the factory
  val factory: RendererFactory
  lazy val rdr: Renderer = factory.get

  def graph(provider: PointProvider): Unit = handle(new PlotHandler {
    override val plot: () => Unit = () => rdr.lines(provider.getPoints(rdr.display))
    override val callback: PlotEvent => Boolean = (e: PlotEvent) => {
      if (e == PlotEventSpace) provider.zoom(2.0f) else false
    }
  })

  def handle(handler: PlotHandler): Unit = {
    handler.plot()
    rdr.refresh()
    var waiting = true
    while (waiting) {
      rdr.nextEvent() match {
        case PlotEventNone =>
        case PlotEventEscape => waiting = false
        case e => if (handler.callback(e)) handler.plot(); rdr.refresh()
      }
    }
    rdr.close()
  }

  def withSettings(params: Seq[PlotParam]) : Plotter = {
    val settings = PlotSettings(params)
    settings.eval(PlotColor, (v: Color) => rdr.color(v))
    settings.eval(PlotLineWidth, (v: Float) => rdr.lineWidth(v))
    this
  }
}

object Plotter {

  class FunctionPointProvider(f: Double => Double) extends PointProvider {
    private var scale = 1.0f

    override def zoom(factor: Float): Boolean = {
      scale *= factor // TODO: should be capped
      true
    }

    // generate 1 point per horizontal pixel
    override def getPoints(display: Display): Seq[Point] = {
      (0 to display.width).map { n =>
        val x = display.pmin.x + (display.pmax.x - display.pmin.x) * n / display.width
        val y = f(x * scale).toFloat
        Point(x,y)
      }
    }

  }
}
