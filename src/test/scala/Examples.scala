import fr.braux.myscala.Plotdef.{PlotTitle, _}
import fr.braux.myscala.{LangtonAnt, Plotting}

import java.awt.Color
import scala.annotation.tailrec


object PlotDoubleFunction extends App with Plotting {
  val f = (x: Double) => Math.sin(4 * x)
  f.plot(PlotRenderer -> "Swing", PlotLineWidth -> 2.0, PlotColor -> Color.red, PlotTitle -> "Some graph")
}

object PlotScalarFunction extends App with Plotting {
  def mandelbrot(cr: Double, ci: Double, limit: Int = 200): Double = {
    @tailrec def rec(zr: Double, zi: Double, n: Int): Int = if (n >= limit) n
      else if (zr * zr + zi * zi > 2.0) n - 1
      else rec(cr + zr * zr - zi * zi, ci + 2 * zr * zi, n + 1)
    rec(cr, ci , 1).toDouble / limit  // expecting a value between 0 and 1
  }
  val f = (x: Double, y: Double) => mandelbrot(x, y)
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