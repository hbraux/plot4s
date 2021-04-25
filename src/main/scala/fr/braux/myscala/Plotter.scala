package fr.braux.myscala

import fr.braux.myscala.Plotdef._

/**
 * This is the main implementation which is agnostic of Graphic API
 */
abstract class Plotter {

  // the Renderer is instanciated lazily from the factory
  val factory: RendererFactory
  lazy val rdr: Renderer = factory.get

  def plotTiles(board: PlottableBoard) : Boolean = {
    val w = (rdr.display.dx min rdr.display.dy) / board.size
    for {i <- 0 until board.size
         j <- 0 until board.size
         if board.value(i, j) > 0} {
      val p = Point(rdr.display.xmin + w * i, rdr.display.ymin + w * j)
      rdr.quad(p, p.copy(x = p.x + w), p.copy(x = p.x + w, y = p.y + w), p.copy(y = p.y + w))
    }
    true
  }

  def plotGraph(fun: PlottableFunction): Boolean = {
    //  generate 1 point per horizontal pixel
    val points = (0 to rdr.display.width).map { i =>
      val x = rdr.display.xmin + rdr.display.dx * i / rdr.display.width
      val y = fun.fx(x * fun.scale).toFloat / fun.scale
      Point(x,y) }
    rdr.lines(points)
    true
  }


  def plot(p: Plottable): Unit = {
    p.render(this)
    rdr.refresh()
    var waiting = true
    var nextTick = System.currentTimeMillis + p.timer
    while (waiting) {
      val timeout = System.currentTimeMillis > nextTick
      (if (timeout) PlotEventTimer else rdr.nextEvent()) match {
        case PlotEventNone =>
        case PlotEventEscape => waiting = false
        case e => if (p.handler(e)) {
          p.render(this)
          rdr.refresh()
        }
      }
      if (timeout)
        nextTick = System.currentTimeMillis + p.timer
    }
    rdr.close()
  }

  def withSettings(params: Seq[PlotParam]) : Plotter = {
    val settings = PlotSettings(params)
    settings.eval(PlotColor, (v: Color) => rdr.color(v))
    settings.eval(PlotLineWidth, (v: Float) => rdr.lineWidth(v))
    settings.eval(PlotTimer, (v: Int) => Plotter.defaultTimer = v)
    settings.eval(PlotScale, (v: Float) => Plotter.defaultScale = v)
    this
  }
}

object Plotter {
  var defaultScale = 2.0f
  var defaultTimer = 1000
}
