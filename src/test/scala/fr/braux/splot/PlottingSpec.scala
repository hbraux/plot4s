package fr.braux.splot

import fr.braux.splot.Plotdef._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

import java.io.FileOutputStream
import scala.annotation.tailrec

/**
 * This class provides examples that can be run manually and is also used to generate the README
 */
class PlottingSpec extends WordSpec with Matchers with BeforeAndAfterEach with Plotting {

  private val saveImages = Option(System.getenv("SAVE")).getOrElse("0") == "1"

  override def beforeEach(): Unit = {
    Plotter.defaultToRaw = saveImages
  }

  "Plotting" should {

    "plot a Double Function" in {
      val f = (x: Double) => Math.sin(4 * x)
      val rdr = f.plot(PlotTitle -> "Sine")
      save(rdr) shouldBe true
    }

    "plot a Scalar Function" in {
      def mandelbrot(cr: Double, ci: Double, limit: Int = 200): Double = {
        @tailrec def rec(zr: Double, zi: Double, n: Int): Int = if (n >= limit) n
        else if (zr * zr + zi * zi > 2.0) n - 1
        else rec(cr + zr * zr - zi * zi, ci + 2 * zr * zi, n + 1)
        1.0 - rec(cr, ci, 1).toDouble / limit // expects a value between 0 and 1
      }
      val f = (x: Double, y: Double) => mandelbrot(x, y)
      val rdr = f.plot(PlotTitle -> "Mandelbrot")
      save(rdr) shouldBe true
    }

    "plot an Array" in {
      var i = -1
      val board = Array.fill(8, 8) {
        i += 1; (i / 8 + i) % 2
      }
      val rdr = board.plot()
      save(rdr) shouldBe true
    }

    "plot an animation" in {
      val ant = new LangtonAnt(80)
      val rdr = ant.plot(PlotTimer -> 10)
      save(rdr) shouldBe true
    }
  }

  private def save(rdr: Renderer): Boolean = {
    if (saveImages) {
      val os = new FileOutputStream(s"images/${rdr.title.replace(' ','_')}.png")
      try {
        os.write(rdr.getRaw)
      } finally {
        os.close()
      }
    }
    true
  }

}