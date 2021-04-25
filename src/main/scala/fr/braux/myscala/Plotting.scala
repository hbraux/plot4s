package fr.braux.myscala


import fr.braux.myscala.Plotdef._

import scala.language.implicitConversions

trait Plotting  {

  private var settings = PlotSettings()

  def plotSettings(params: PlotParam*): Unit = settings = PlotSettings(params)

  implicit lazy val plotter: Plotter = new Plotter {
    override val factory: RendererFactory = new RendererFactory {
      override val get: Renderer = if (settings.get(PlotApiConsole, false)) ConsoleRenderer(settings) else OpenGLRenderer(settings)
    }
  }

  implicit def functionToPlottable(f: Double => Double): PlottableFunction = new PlottableFunction() {
    override def fx: Double => Double = f
  }


}
