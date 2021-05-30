package fr.braux.plot4s

import fr.braux.plot4s.PlotValueType.{BoolValue, ColorValue, ConstValue, FloatValue, IntValue, NoValue, StringValue}
import fr.braux.plot4s.Plotdef.PlotConst

import java.awt.Color
import scala.reflect.ClassTag

case class PlotParams(settings: Map[PlotConst, Any]) {
  def get[A](c: PlotConst, orElse: A)(implicit t: ClassTag[A]): A = {
    settings.get(c) match {
      case Some(v: A) => v
      case _ => orElse
    }
  }
  def eval[A](c: PlotConst, f: A => Unit)(implicit t: ClassTag[A]): Unit = settings.get(c) match {
    case Some(v: A) => f(v)
    case _ =>
  }
}

object PlotParams {
  def apply(xs: (PlotConst, Any)*) : PlotParams = PlotParams(xs)
  def apply(xs: Iterable[(PlotConst, Any)] = Seq.empty): PlotParams = PlotParams(
    xs.map { x =>
      (x._1.valueType, x._2) match {
        case (StringValue, _: String) => x
        case (IntValue, _: Int) => x
        case (FloatValue, _: Float) => x
        case (FloatValue, v: Double) => (x._1, v.toFloat)
        case (ColorValue, _: Color) => x
        case (BoolValue, _: Boolean) => x
        case (ConstValue, _: PlotConst) => x
        case (_, NoValue) => throw new IllegalArgumentException(s"${x._1} is not a parameter")
        case _ => throw new IllegalArgumentException(s"Expecting a type ${x._1.valueType.toString.replace("Value","")} for ${x._1}")
      }
    }.toMap)
}