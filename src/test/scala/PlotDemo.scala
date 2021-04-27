import fr.braux.myscala.Plotdef._
import fr.braux.myscala.{LangtonAnt, Plotting}
import org.scalatest.{Matchers, WordSpec}

object PlotDemo {

  object PlotGraph extends App with Plotting {
    val f = (x: Double) => Math.sin(2*x) + Math.cos(x)
    f.plot(PlotColor -> Red, PlotLineWidth -> 2.0, PlotScale -> 4.0f)
  }


}

