package fr.braux.myscala


import fr.braux.myscala.Plotdef._

import scala.language.implicitConversions

trait Plotting  {


  implicit def functionToPlottable(f: Double => Double): PlottableMathFunction = new PlottableMathFunction() {
    override def apply(x: Double): Double = f(x)
  }

  implicit def arrayToPlottable(a: Array[Array[Int]]): PlottableMatrix[Int] = new PlottableMatrix[Int]() {
    override val rows: Int = a.length
    override val columns: Int = a(0).length
    override def apply(r: Int, c: Int): Int = a(r)(c)
  }
}
