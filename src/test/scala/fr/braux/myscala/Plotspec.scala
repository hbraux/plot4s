package fr.braux.myscala

import org.scalatest.{Matchers, WordSpec}

import fr.braux.myscala.Plotdef._

class Plotspec extends  WordSpec with Matchers with Plotting {

  plotSettings(PlotApiOpenGl -> true, PlotWindowWidth -> 800, PlotWindowHeight -> 400, PlotWindowTitle -> "Plotspec")

  "Plot" should {
    "plot a graph from Real function"  in {
      val f = (x: Double) => Math.sin(2*x) + Math.cos(x)
      f.plot(PlotColor -> Red, PlotLineWidth -> 2.0, PlotScale -> 4.0f)
    }

    "plot a bord" in {
      val board: Array[Array[Int]] = Array.fill(8,8)(0)
      board(4)(4) = 1
      board.plot()
    }
  }
}
