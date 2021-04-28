package fr.braux.myscala


import fr.braux.myscala.Plotdef._

import scala.math.abs

case class ConsoleRenderer private(width: Int, height: Int) extends Renderer {

  override type Texture = Char
  private val noTexture = ' '
  private val defaultTexture = 'X'
  private var texture: Texture = noTexture
  private var window = Array.fill(height, width)(noTexture)

  override val background: Color = White
  override val minPoint: Point = Point(0,0)
  override val maxPoint: Point = Point(width, height)

  override def useColor(c: Color): Unit = if (c != background) texture = defaultTexture

  override def point(a: Point): Unit = point(a.y.toInt, a.x.toInt)

  private def point(r: Int, c: Int): Unit = window(r)(c) = defaultTexture

  override def line(a: Point, b: Point): Unit = {
    if (a.x == b.x) vline(a.y.toInt min b.y.toInt, a.x.toInt, abs(a.y.toInt - b.y.toInt))
    else if (a.y == b.y) hline(a.x.toInt min b.x.toInt, a.y.toInt, abs(a.x.toInt - b.x.toInt))
    else throw new NotImplementedError("not supported yet")
  }

  private def vline(r: Int, c: Int, length: Int): Unit = (0 to length).foreach(i => point(r + i, c))
  private def hline(r: Int, c: Int, length: Int): Unit = (0 to length).foreach(i => point(r, c + i))

  override def triangle(a: Point, b: Point, c: Point): Unit = throw new NotImplementedError("not supported")

  override def quad(a: Point, b: Point, c: Point, d: Point): Unit = throw new NotImplementedError("not supported")

  override def close(): Unit = {} // do nothing

  override def load(filePath: String): Texture = noTexture // do nothing

  override def useTexture(t: Texture): Unit = texture = t

  override def refresh(): Unit = window = Array.fill(height, width)(noTexture)

  override def lines(points: Iterable[Point]): Unit = throw new NotImplementedError("not supported yet")

  override def useLine(width: Float): Unit = {} // do nothing

  override def nextEvent(): PlotEvent = PlotEventNone

  override def getRaw: Array[Byte] = window.map(_.mkString).mkString("\n").getBytes
}

