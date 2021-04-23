package fr.braux.myscala


import fr.braux.myscala.Plotlib._
import fr.braux.myscala.Plotter._

trait Plotting  {


  private var plotApi: PlotConst = PlotApiOpenGl

  def useApi(api: PlotConst): Unit = plotApi = api

  lazy val plotter: Plotter = new Plotter {
    override val rdr: Renderer = plotApi match {
      case PlotApiOpenGl => OpenGLRenderer()
      case PlotApiConsole => ConsoleRenderer()
    }
  }

  class PlottableFunction(f: Double => Double) {
    def plot(params: PlotParam*): Unit = plotter.plotGraph(new MathFunctionPointProvider(f), params)
  }

  implicit def convertToPlottable(f: Double => Double): PlottableFunction = new PlottableFunction(f)
}
