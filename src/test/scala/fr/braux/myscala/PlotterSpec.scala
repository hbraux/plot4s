package fr.braux.myscala

import org.scalatest.{Matchers, WordSpec}

import fr.braux.myscala.Plotdef._

class PlotterSpec extends  WordSpec with Matchers with Plotting {

  "Plot" should {
    "plot a graph from Real function"  in {
      val f = (x: Double) => Math.sin(2*x) + Math.cos(x)
      val bytes = f.plot(PlotRenderer -> PlotConsoleRenderer, PlotBinary -> true)
      new String(bytes) shouldEqual "hello"
    }

    "plot a matrix" in {
      val size = 8
      var i = -1
      val board = Array.fill(size,size){ i += 1; (i/size + i) %2 }
      val bytes =  board.plot(PlotRenderer -> PlotConsoleRenderer, PlotBinary -> true)
      new String(bytes) shouldEqual "hello"
    }

  }

}
