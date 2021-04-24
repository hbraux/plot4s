package fr.braux.myscala


import fr.braux.myscala.Plotlib._
import fr.braux.myscala.Plotter._

import scala.language.implicitConversions

trait Plotting  {


  private var settings = PlotSettings()

  def plotSettings(params: PlotParam*): Unit =  settings = PlotSettings(params)

  lazy val plotter: Plotter = new Plotter {
    override val factory: RendererFactory = new RendererFactory {
      override val get: Renderer = if (settings.get(PlotApiConsole, false)) ConsoleRenderer(settings) else OpenGLRenderer(settings)
    }
  }

  class PlottableFunction(f: Double => Double) {
    def plot(params: PlotParam*): Unit = plotter.withSettings(params).graph(new FunctionPointProvider(f))
  }

  implicit def convertToPlottable(f: Double => Double): PlottableFunction = new PlottableFunction(f)
}
