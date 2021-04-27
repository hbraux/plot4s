import fr.braux.myscala.Plotdef._
import fr.braux.myscala.Plotting



object PlotMathFunction extends App with Plotting {
  val f = (x: Double) => Math.sin(2 * x) + Math.cos(x)
  f.plot(PlotScale -> 4.0f, PlotLineWidth -> 2.0)
}


object PlotEmptyChessBoard extends App with Plotting {
  val size = 8
  var i = -1
  val board = Array.fill(size,size){ i += 1; (i/size + i) %2 }
  board.plot()
}


