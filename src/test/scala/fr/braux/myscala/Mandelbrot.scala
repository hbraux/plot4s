package fr.braux.myscala

class Mandelbrot(threshold: Int) {

  private def compute(xc: Double, yc: Double): Int = {
    var n = 0
    var (x, y) = (0.0, 0.0)
    while (x * x + y * y < 2 && n < threshold) {
      val (xt, yt) = (xc + x * x - y * y,yc + 2 * x * y)
      x = xt
      y = yt
      n += 1
    }
    n
  }

}
