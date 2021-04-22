package fr.braux.myscala

import org.scalatest.{Matchers, WordSpec}

class Plotpec extends  WordSpec with Matchers with Plotting {

  "Plot" should {
    "plot a graph" in {
      val f = (x: Double) => Math.sin(x)
      f.plot()
    }
  }
}
