package fr.braux.myscala

import fr.braux.myscala.Plotdef.{PlotAnimation, PlottableBoard}

class LangtonAnt(val size: Int) extends PlottableBoard with PlotAnimation {
  private val white = ' '
  private val black = 'X'
  private val m = Array.fill(size, size)(white)
  private var x = size / 2
  private var y = size / 2
  private var d = 0 // 0: North, 1: East, 2: South, 3: West

  override def next(): Boolean = {
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

  override def value(col: Int, row: Int): Int = if (m(col)(row) == black) 1 else 0

  def print(): Unit = m.foreach(x => println(x.mkString))
}

