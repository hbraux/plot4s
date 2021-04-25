package fr.braux.myscala

import fr.braux.myscala.Plotdef.PlottableBoard

class LangtonAnt(val size: Int) extends PlottableBoard {
  private val m = Array.fill(size, size)(false)
  private var x = size / 2
  private var y = size / 2
  private var dir = 0 // 0: North, 1: East, 2: South, 3: West

  override def next(): Boolean = {
    dir = Math.floorMod(dir + (if (m(x)(y)) -1 else 1), 4)
    dir match {
      case 0 => if (y - 1 >= 0) y -= 1 else return false
      case 1 => if (x + 1 < size) x += 1 else return false
      case 2 => if (y + 1 < size) y += 1 else return false
      case 3 => if (x - 1 >= 0) x -= 1 else return false
    }
    m(x)(y) = !m(x)(y)
    true
  }

  override def plotValue(col: Int, row: Int): Int = if (m(col)(row)) 1 else 0
}

