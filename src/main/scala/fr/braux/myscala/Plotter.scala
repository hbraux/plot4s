package fr.braux.myscala

import fr.braux.myscala.Plotdef._

import java.awt.Color


/**
 * This is the main implementation which is agnostic of Graphic API
 */
class Plotter (params: PlotParams) {

  private var scale = 1.0f
  private var timer = 1000

  // the Renderer is created lazily
  lazy val renderer: Renderer = {
    val factory = RendererFactory(params.get(PlotRenderer, "OpenGL"))
    scale *= factory.defaultSize / 10 // adjust the scale to size
    factory(params.get(PlotTitle,"Plot"), params.get(PlotWidth,factory.defaultSize), params.get(PlotHeight,factory.defaultSize), params.get(PlotBackground,factory.defaultBackground))
  }

  def tiles[T](matrix: PlottableMatrix[T]) : Unit = {
    renderer.useTiles(matrix.rows, matrix.columns)
    for {r <- 0 until matrix.rows; c <- 0 until matrix.columns} {
      renderer.tile(r, c, matrix(r, c) match {
        case i: Int if i >= 0 && i < BasicColors.length => BasicColors(i)
        case f: Float if f >=0 && f <= 1 => new Color(0f, 0f, f)
        case f: Float if f < 0 && f >= -1 => new Color(-f, 0f, 0f)
        case _ => Color.lightGray
      })
    }
  }

  def graph(f: PlottableMathFunction): Unit =
    renderer.points((0 until renderer.width).map(x => Point(x, (renderer.height/2 + f(x / scale).toFloat * scale).toInt)), joined = true)


  def plot(p: Plottable): Renderer = {
    params.eval(PlotTimer, (v: Int) => timer = v)
    params.eval(PlotScale, (v: Float) => scale = v)
    renderer.useParams(params)
    p.render(this)
    if (params.get(PlotToRaw, false))
      return renderer
    renderer.swap()
    def replot(): Unit = { renderer.clear(); p.render(this); renderer.swap() }
    var nextTick = System.currentTimeMillis + timer
    var waiting = true
    while (waiting) {
      val timeout = System.currentTimeMillis > nextTick
      (if (timeout) PlotEventTimer else renderer.nextEvent()) match {
        case PlotEventNone =>
        case PlotEventEscape => waiting = false
        case e if p.handlers contains e => p.handlers.get(e).foreach(h => if (h.apply()) renderer.swap())
        case PlotEventPageUp   => timer /= 2
        case PlotEventPageDown => timer *= 2
        case PlotEventSpace    => timer = timer ^ 0xffffff // pause (high timer value)
        case PlotEventUp       => scale /= 2; replot()
        case PlotEventDown      => scale *= 2; replot()
        case PlotEventTimer => p match {
          case x: Playable if x.playNext() => replot()
          case x: Playable => waiting = false
          case _ =>
        }
      }
      if (timeout)
        nextTick = System.currentTimeMillis + timer
    }
    renderer.close()
    renderer
  }


}

object Plotter {
  def apply(xs: (PlotConst, Any)*): Plotter = Plotter(xs)
  def apply(xs: Iterable[(PlotConst, Any)]): Plotter = new Plotter(PlotParams(xs))
}
