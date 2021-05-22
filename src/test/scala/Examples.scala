import fr.braux.myscala.Plotdef.{PlotTitle, _}
import fr.braux.myscala.{LangtonAnt, Mandelbrot, Plotting}

import java.awt.Color


object PlotDoubleFunction extends App with Plotting {
  val f = (x: Double) => Math.sin(4 * x)
  f.plot(PlotRenderer -> "Swing", PlotLineWidth -> 2.0, PlotColor -> Color.red, PlotTitle -> "Some graph")
}

object PlotScalarFunction extends App with Plotting {
  val f = (x: Double, y: Double) => Mandelbrot(x, y, 200)
  f.plot(PlotRenderer -> "Swing")
}

object PlotEmptyChessBoardSwing extends App with Plotting {
  var i = -1
  val board = Array.fill(8,8){ i += 1; (i/8 + i) %2 }
  board.plot(PlotRenderer -> "Swing", PlotTitle -> "Chess")
}

object PlotEmptyChessBoardOpenGL extends App with Plotting {
  var i = -1
  val board = Array.fill(8,8){ i += 1; (i/8 + i) %2 }
  board.plot(PlotRenderer -> "OpenGL", PlotTitle -> "Chess")
}


object PlotLangontAntSwing extends App  {
  val ant = new LangtonAnt(80)
  ant.plot(PlotRenderer -> "Swing", PlotTimer -> 10, PlotTitle -> "Langton's Ant")
}

object PlotLangontAntOpenGL extends App  {
  val ant = new LangtonAnt(80)
  ant.plot(PlotRenderer -> "OpenGL", PlotTimer -> 10, PlotTitle -> "Langton's Ant")
}