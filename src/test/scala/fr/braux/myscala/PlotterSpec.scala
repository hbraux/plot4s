package fr.braux.myscala

import org.scalatest.{Matchers, WordSpec}

import fr.braux.myscala.Plotdef._

class PlotterSpec extends  WordSpec with Matchers with Plotting {

  "Plot" should {
    "plot a graph from Real function"  in {
      val f = (x: Double) => Math.sin(2*x) + Math.cos(x)
      val rdr = f.plot(PlotRenderer -> PlotConsoleRenderer, PlotToRaw -> true, PlotWidth -> 10, PlotHeight -> 10)
      new String(rdr.getRaw) shouldEqual "hello"
    }

    "plot a matrix" in {
      val size = 4
      var i = -1
      val board = Array.fill(size,size){ i += 1; (i/size + i) %2 }
      val rdr = board.plot(PlotRenderer -> PlotConsoleRenderer, PlotToRaw -> true, PlotWidth -> size, PlotHeight -> size)
      new String(rdr.getRaw) shouldEqual " X X\nX X \n X X\nX X ".stripMargin
    }

  }

}
