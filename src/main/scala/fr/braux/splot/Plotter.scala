package fr.braux.splot

import fr.braux.splot.Plotdef._

import java.awt.Color
import java.awt.image.BufferedImage


/**
 * This is the main implementation which is agnostic of Graphic API
 */
class Plotter (params: PlotParams) {
  import Plotter._

  private var scale = 2.0f // a scale of 2 means that display is [-1,-1] to [1,1]
  private var timer = 1000

  // the Renderer is created lazily
  lazy val renderer: Renderer = {
    val factory = RendererFactory(params.get(PlotRenderer, defaultRenderer))
    factory(params.get(PlotTitle,defaultTitle),
      params.get(PlotWidth,factory.defaultSize),
      params.get(PlotHeight,factory.defaultSize),
      params.get(PlotBackground,factory.defaultBackground))
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

  def graph(f: PlottableDoubleFunction): Unit = {
    renderer.points((0 until renderer.width).map { i =>
      val x = (i.toFloat / renderer.width - 0.5) * scale
      val y = f(x).toFloat
      Point(i, ((0.5 - y / scale) * renderer.height).toInt)
    }, joined = true)
  }

  def image(f: PlottableScalarFunction): Unit = {
    val image = new BufferedImage(renderer.width, renderer.height, BufferedImage.TYPE_INT_RGB)
    for (i <- 0 until renderer.width; j <- 0 until renderer.height) {
      val x = (i.toFloat/renderer.width - 0.5) * scale
      val y = (j.toFloat/renderer.height - 0.5) * scale
      val z = f(x, y).toFloat
      image.setRGB(i, j, Color.getHSBColor(z * 256, 1f, z).getRGB)
    }
    renderer.image(image)
  }


  def plot(p: Plottable): Renderer = {
    applyParams()
    p.render(this)
    renderer.swap()
    if (params.get(PlotToRaw, defaultToRaw))
      return renderer
    def replot(): Unit = {
      renderer.clear()
      p.render(this)
      renderer.swap()
    }
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

  private def applyParams(): Unit = {
    params.eval(PlotTimer, (v: Int) => timer = v)
    params.eval(PlotScale, (v: Float) => scale = v)
    renderer.useParams(params)
  }


}

object Plotter {
  var defaultRenderer = "Swing"
  val defaultTitle = "Scala Plot"
  var defaultToRaw = false

  def apply(xs: (PlotConst, Any)*): Plotter = Plotter(xs)
  def apply(xs: Iterable[(PlotConst, Any)]): Plotter = new Plotter(PlotParams(xs))
}
