import fr.braux.myscala.Plotdef.{PlotTitle, _}
import fr.braux.myscala.{LangtonAnt, Plotting}


object PlotMathFunction extends App with Plotting {
  val f = (x: Double) => Math.sin(2 * x) + Math.cos(x)
  f.plot(PlotScale -> 4.0f, PlotLineWidth -> 2.0, PlotColor -> Red, PlotTitle -> "Some graph")
}

object PlotEmptyChessBoard extends App with Plotting {
  var i = -1
  val board = Array.fill(8,8){ i += 1; (i/8 + i) %2 }
  board.plot(PlotTitle -> "Chess")
}

object PlotLangontAnt extends App  {
  val ant = new LangtonAnt(80)
  ant.plot(PlotTimer -> 0, PlotTitle -> "Langton's Ant")
}

