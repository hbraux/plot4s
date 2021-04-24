package fr.braux.myscala

import fr.braux.myscala.Plotdef._

/**
 * This is the main implementation which is agnostic of Graphic API
 */
abstract class Plotter {

  // the Renderer is instanciated lazily from the factory
  val factory: RendererFactory
  lazy val rdr: Renderer = factory.get
  private var timer = 1000

  def graph(provider: PointProvider): Unit = handle(new PlotHandler {
    override val plot: () => Unit = () => rdr.lines(provider.getPoints(rdr.display))
    override val callback: PlotEvent => Boolean = {
      case PlotEventPageUp => provider.zoom(2f)
      case PlotEventPageDown => provider.zoom(0.5f)
      case _ => false
    }})

  def board() : Unit = {

  }

  def handle(handler: PlotHandler): Unit = {
    handler.plot()
    rdr.refresh()
    var waiting = true
    var nextTick = System.currentTimeMillis + timer
    while (waiting) {
      val timeout = System.currentTimeMillis > nextTick
      (if (timeout) PlotEventTimer else rdr.nextEvent()) match {
        case PlotEventNone =>
        case PlotEventEscape => waiting = false
        case e => if (handler.callback(e)) {
          handler.plot()
          rdr.refresh()
        }
      }
      if (timeout)
        nextTick = System.currentTimeMillis + timer
    }
    rdr.close()
  }

  def withSettings(params: Seq[PlotParam]) : Plotter = {
    val settings = PlotSettings(params)
    settings.eval(PlotColor, (v: Color) => rdr.color(v))
    settings.eval(PlotLineWidth, (v: Float) => rdr.lineWidth(v))
    settings.eval(PlotTimer, (v: Int) => timer = v)
    settings.eval(PlotScale, (v: Float) => Plotter.defaultScale = v)
    this
  }
}

object Plotter {
  private var defaultScale = 1.0f

  class FunctionPointProvider(f: Double => Double) extends PointProvider {
    private var scale = defaultScale

    override def zoom(factor: Float): Boolean = {
      scale = scale / factor  // TODO: should be capped
      true
    }

    // generate 1 point per horizontal pixel
    override def getPoints(display: Display): Seq[Point] = {
      (0 to display.width).map { n =>
        val x = display.pmin.x + (display.pmax.x - display.pmin.x) * n / display.width
        val y = f(x * scale).toFloat / scale
        Point(x,y)
      }
    }

  }
}
