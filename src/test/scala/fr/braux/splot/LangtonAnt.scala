package fr.braux.splot

import fr.braux.splot.Plotdef.{Playable, PlottableMatrix}

class LangtonAnt(val size: Int) extends PlottableMatrix[Int] with Playable {
  private val white: Byte = 0
  private val black: Byte = 1
  private val m = Array.fill(size, size)(white)
  private var x = size / 2
  private var y = size / 2
  private var d = 0 // 0: North, 1: East, 2: South, 3: West

  override def playNext(): Boolean = {
    if (m(x)(y) == black) {
      m(x)(y) = white
      d = Math.floorMod(d + 1, 4)
    } else {
      m(x)(y) = black
      d = Math.floorMod(d - 1, 4)
    }
    d match {
      case 0 => if (y - 1 >= 0) y -= 1 else return false
      case 1 => if (x + 1 < size) x += 1 else return false
      case 2 => if (y + 1 < size) y += 1 else return false
      case 3 => if (x - 1 >= 0) x -= 1 else return false
    }
    true
  }
  override def apply(row: Int, col: Int): Int = m(row)(col)
  override def rows: Int = size
  override def columns: Int = size
}

