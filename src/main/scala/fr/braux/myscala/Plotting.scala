package fr.braux.myscala


import fr.braux.myscala.Plotdef._

import scala.language.implicitConversions

trait Plotting  {


  implicit def functionToPlottable(f: Double => Double): PlottableRealFunction = new PlottableRealFunction() {
    override def apply(x: Double): Double = f(x)
  }

}
