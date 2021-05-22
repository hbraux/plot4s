package fr.braux.myscala

object Mandelbrot {
  def apply(xc: Double, yc: Double, threshold: Int = 50): Double = {
    var n = 0
    var (x, y) = (0.0, 0.0)
    while (x * x + y * y < 2 && n < threshold) {
      val (xt, yt) = (xc + x * x - y * y, yc + 2 * x * y)
      x = xt
      y = yt
      n += 1
    }
    n.toDouble / threshold
  }

}

