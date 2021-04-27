package fr.braux.myscala

import fr.braux.myscala.Plotdef._

/**
 * This is the main implementation which is agnostic of Graphic API
 */
class Plotter (params: PlotParams) {
  private var scale = 2.0f
  private var timer = 1000
  private var background: Color = White

  lazy val renderer: Renderer = if (params.get(PlotConsole, false)) StringRenderer(params) else OpenGLRenderer(params)

  def plotTiles[T](matrix: PlottableMatrix[T]) : Boolean = {
    val wx = renderer.dx / matrix.columns
    val wy = renderer.dy / matrix.rows
    for {r <- 0 until matrix.rows; c<- 0 until matrix.columns} {
      matrix(r, c) match {
        case i: Int if i >= 0 && i < Colors.length && Colors(i) != background => renderer.color(Colors(i))
        case f: Float if f >=0 && f <= 1 => renderer.color(Color(blue = f))
        case f: Float if f <0 && f >= -1 => renderer.color(Color(red = -f))
        case _ =>
      }
      val p = Point(renderer.xmin + wx * c, renderer.ymin + wy * r)
      renderer.quad(p, p.copy(x = p.x + wx), p.copy(x = p.x + wx, y = p.y + wy), p.copy(y = p.y + wy))
    }
    true
  }

  def plotGraph(f: PlottableRealFunction): Boolean = {
    //  generate 1 point per horizontal pixel
    val points = (0 to renderer.width).map { i =>
      val x = renderer.xmin + renderer.dx * i / renderer.width
      val y = f(x * scale).toFloat / scale
      Point(x,y) }
    renderer.lines(points)
    true
  }


  def plot(p: Plottable): Unit = {

    params.eval(PlotBackground, (v: Color) => background = v)
    params.eval(PlotColor, (v: Color) => renderer.color(v))
    params.eval(PlotLineWidth, (v: Float) => renderer.lineWidth(v))
    params.eval(PlotTimer, (v: Int) => timer = v)
    params.eval(PlotScale, (v: Float) => scale = v)

    p.render(this)
    renderer.refresh()
    var waiting = renderer.supportEvents
    var nextTick = System.currentTimeMillis + timer
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
        case PlotEventUp => scale *= 2; renderer.refresh()
        case PlotEventTimer => p match {
          case x: Playable if x.next() => renderer.refresh()
          case _ =>
        }
      }
      if (timeout)
        nextTick = System.currentTimeMillis + timer
    }
    renderer.close()
  }

}

object Plotter {
  def apply(xs: (PlotConst, Any)*): Plotter = Plotter(xs)
  def apply(xs: Iterable[(PlotConst, Any)]): Plotter = new Plotter(PlotParams(xs))
}
