package fr.braux.plot4s

import scala.annotation.tailrec

// example from the README
object Mandelbrot extends App with Plotting {
  val limit = 200
  val mandelbrot = (cr: Double, ci: Double) => {
    @tailrec def rec(zr: Double, zi: Double, n: Int): Int = if (n >= limit) n
    else if (zr * zr + zi * zi > 2.0) n - 1
    else rec(cr + zr * zr - zi * zi, ci + 2 * zr * zi, n + 1)
    1.0 - rec(cr, ci, 1).toDouble / limit
  }
  mandelbrot.plot()
}
