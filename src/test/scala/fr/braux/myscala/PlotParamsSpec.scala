package fr.braux.myscala

import fr.braux.myscala.PlotValueType._
import fr.braux.myscala.Plotdef._
import org.scalatest.{Matchers, WordSpec}

class PlotParamsSpec extends WordSpec with Matchers with Plotting {
  private object TestNoValueParam extends PlotConst(NoValue)
  private object TestStringParam extends PlotConst(StringValue)
  private object TestIntParam extends PlotConst(IntValue)
  private object TestFloatParam extends PlotConst(FloatValue)
  private object TestBoolParam extends PlotConst(BoolValue)
  private object TestConstParam extends PlotConst(ConstValue)
  private object TestColorParam extends PlotConst(ColorValue)

  "PlotParams" should {
    "support any param value type" in {
      val params = PlotParams(TestStringParam -> "s", TestIntParam -> 10, TestFloatParam -> 1f, TestBoolParam -> false, TestConstParam -> TestNoValueParam, TestColorParam -> Red)
      params.get(TestStringParam, "S") shouldEqual "s"
      params.get(TestNoValueParam, "S") shouldEqual "S"
      params.get(TestIntParam, 20) shouldEqual 10
      params.get(TestNoValueParam, 20) shouldEqual 20
      params.get(TestFloatParam, 2f) shouldEqual 1f
      params.get(TestBoolParam, true) shouldEqual false
      params.get[PlotConst](TestConstParam, TestStringParam) shouldEqual TestNoValueParam
      params.get(TestColorParam, White) shouldEqual Red
    }
  }

}
