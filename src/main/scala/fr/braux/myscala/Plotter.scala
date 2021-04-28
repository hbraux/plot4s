package fr.braux.myscala

import fr.braux.myscala.Plotdef._


/**
 * This is the main implementation which is agnostic of Graphic API
 */
class Plotter (params: PlotParams) {
  import fr.braux.myscala.Plotter._

  private var scale = 2.0f
  private var timer = 1000

  // the Renderer is created lazily as parameters must be set before
  lazy val renderer: Renderer = buildRender()

  private def buildRender(): Renderer = {
    params.get(PlotRenderer, PlotOpenGLRenderer) match {
      case PlotConsoleRenderer => ConsoleRenderer(params.get(PlotWindowWidth,20), params.get(PlotWindowHeight,20))
      case PlotOpenGLRenderer => OpenGLRenderer(params.get(PlotWindowWidth,400), params.get(PlotWindowHeight,400),  params.get(PlotWindowTitle,"Plot"),
        params.get(PlotBackground, White))
    }
  }

  def tiles[T](matrix: PlottableMatrix[T]) : Unit = {
    renderer.useTiles(matrix.rows, matrix.columns)
    for {r <- 0 until matrix.rows; c <- 0 until matrix.columns} {
      renderer.tile(r, c, matrix(r, c) match {
        case i: Int if i >= 0 && i < Colors.length => Colors(i)
        case f: Float if f >=0 && f <= 1 => Color(blue = f)
        case f: Float if f < 0 && f >= -1 => Color(red = -f)
        case _ => Grey
      })
    }
  }

  def graph(f: PlottableRealFunction): Unit = {
    renderer.lines(renderer.xRange.map(x => Point(x, f(x * scale).toFloat / scale)))
  }


  def plot(p: Plottable): Array[Byte] = {
    params.eval(PlotTimer, (v: Int) => timer = v)
    params.eval(PlotScale, (v: Float) => scale = v)
    renderer.useParams(params)
    p.render(this)
    if (params.get(PlotBinary, false)) {
      return renderer.asBinary()
    }
    renderer.refresh()
    var nextTick = System.currentTimeMillis + timer
    var waiting = true
    while (waiting) {
      val timeout = System.currentTimeMillis > nextTick
      (if (timeout) PlotEventTimer else renderer.nextEvent()) match {
        case PlotEventNone =>
        case PlotEventEscape => waiting = false
        case e if p.handlers contains e => p.handlers.get(e).foreach(h => if (h.apply()) renderer.refresh())
        case PlotEventPageUp   => timer *= 2
        case PlotEventPageDown => timer /= 2
        case PlotEventSpace    => timer = timer ^ 0 // pause (high timer value)
        case PlotEventUp => scale /= 2; renderer.refresh()
        case PlotEventDown => scale *= 2; renderer.refresh()
        case PlotEventTimer => p match {
          case x: Playable if x.next() => renderer.refresh()
          case _ =>
        }
      }
      if (timeout)
        nextTick = System.currentTimeMillis + timer
    }
    renderer.close()
    noBinary
  }

}

object Plotter {
  private val noBinary = "".getBytes
  def apply(xs: (PlotConst, Any)*): Plotter = Plotter(xs)
  def apply(xs: Iterable[(PlotConst, Any)]): Plotter = new Plotter(PlotParams(xs))
}
