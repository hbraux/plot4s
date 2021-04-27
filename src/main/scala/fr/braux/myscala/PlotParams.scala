package fr.braux.myscala

import fr.braux.myscala.PlotValueType.{BoolValue, ColorValue, ConstValue, FloatValue, IntValue, NoValue, StringValue}
import fr.braux.myscala.Plotdef.{Color, PlotConst}

case class PlotParams(settings: Map[PlotConst, Any]) {
  def get[A](c: PlotConst, orElse: A): A = {
    settings.get(c) match {
      case Some(v: A) => v
      case _ => orElse
    }
  }
  def eval[A](c: PlotConst, f: A => Unit): Unit = settings.get(c) match {
    case Some(v: A) => f(v)
    case _ =>
  }
}

object PlotParams {
  def apply(xs: Iterable[(PlotConst, Any)] = Seq.empty): PlotParams = PlotParams(
    xs.map { x =>
      (x._1.valueType, x._2) match {
        case (StringValue, v: String) => x
        case (IntValue, v: Int) => x
        case (FloatValue, v: Float) => x
        case (FloatValue, v: Double) => (x._1, v.toFloat)
        case (ColorValue, v: Color) => x
        case (BoolValue, v: Boolean) => x
        case (ConstValue, v: PlotConst) => x
        case (_, NoValue) => throw new IllegalArgumentException(s"${x._1} is not a parameter")
        case _ => throw new IllegalArgumentException(s"Expecting a type ${x._1.valueType.toString.replace("Value","")} for ${x._1}")
      }
    }.toMap)
}