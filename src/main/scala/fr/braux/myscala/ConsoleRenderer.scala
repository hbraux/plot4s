package fr.braux.myscala

import fr.braux.myscala.Plotdef._

import java.awt.Color
import java.awt.image.BufferedImage

case class ConsoleRenderer private(width: Int, height: Int, background: Color) extends Renderer {

  override type Texture = Char
  private val noTexture = ' '
  private val defaultTexture = 'X'
  private var texture: Texture = noTexture
  private var window = Array.fill(height, width)(noTexture)

  override def useColor(color: Color): Unit = if (color != background) texture = defaultTexture

  override def point(p: Point): Unit = if (p.x >= 0 && p.y >= 0 && p.x < width && p.y < height) draw(p.y, p.x)

  private def draw(row: Int, col: Int): Unit = window(row)(col) = defaultTexture

  override def line(from: Point, to: Point): Unit = {
    if (from.x == to.x) vline(from.y min to.y, from.x, Math.abs(from.y - to.y))
    else if (from.y == to.y) hline(from.x min to.x, from.y, Math.abs(from.x - to.x))
    else throw new NotImplementedError("not supported yet")
  }
  private def vline(row: Int, col: Int, l: Int): Unit = (0 to l).foreach(i => draw(row + i, col))
  private def hline(row: Int, col: Int, l: Int): Unit = (0 to l).foreach(i => draw(row, col + i))

  // joined not yet suppoted
  override def points(ps: Iterable[Point], joined: Boolean): Unit = ps.foreach(point)

  override def rect(p: Point, w: Int, h: Int): Unit =
    for (col <- 0 until h; row <- 0 until w) draw(p.y + col, p.x + row)

  override def close(): Unit = {} // do nothing

  override def load(filePath: String): Texture = noTexture // do nothing

  override def useTexture(t: Texture): Unit = texture = t

  override def clear(): Unit = window = Array.fill(height, width)(noTexture)

  override def swap(): Unit = {
    print("\\u001b[H\\u001b[2J")
    System.out.flush()
  }

  override def useLine(width: Float): Unit = {} // do nothing

  override def nextEvent(): PlotEvent = PlotEventNone

  override def getRaw: Array[Byte] = window.map(_.mkString).mkString("\n").getBytes

  override def image(img: BufferedImage): Unit = {}
}

object ConsoleRenderer extends RendererFactory {
  override val defaultSize: Int = 30 // multiple of 2,3 and 5
  override val defaultBackground: Color = Color.white
  override def apply(title: String, width: Int, height: Int, background: Color): Renderer = ConsoleRenderer(width, height, background)
}