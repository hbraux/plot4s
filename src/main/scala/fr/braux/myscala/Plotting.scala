package fr.braux.myscala

import fr.braux.myscala.Plot.Renderer

import scala.language.implicitConversions


trait Plotting {

  lazy val plotter: Plot = new Plot {
    override val rdr: Renderer = GLRenderer()
  }

  class PlottableFunction(f: Double => Double) {
    def plot(): Unit = plotter.plotGraph(f)
  }

  implicit def convertToPlottable(f: Double => Double): PlottableFunction = new PlottableFunction(f)
}
