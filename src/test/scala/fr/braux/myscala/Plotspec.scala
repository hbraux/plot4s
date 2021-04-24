package fr.braux.myscala

import org.scalatest.{Matchers, WordSpec}

import fr.braux.myscala.Plotlib._

class Plotspec extends  WordSpec with Matchers with Plotting {

  plotSettings(PlotApiOpenGl -> true, PlotWindowWidth -> 400, PlotWindowHeight -> 400)

  "Plot" should {
    "plot a graph" in {
      val f = (x: Double) => Math.sin(4 * x)
      f.plot(PlotLineColor -> Red, PlotLineWidth -> 2.0)
    }
  }
}
