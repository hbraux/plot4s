package fr.braux.myscala


import fr.braux.myscala.Plotdef._

case class StringRenderer private(width: Int, height: Int) extends Renderer {

  override type Texture = Char
  private val noTexture = ' '
  private var texture: Texture = noTexture
  override val background: Color = Black

  override val supportEvents: Boolean = false
  override val minPoint: Point = Point(0,0)
  override val maxPoint: Point = Point(width, height)

  override def useColor(c: Color): Unit = ???

  override def point(a: Point): Unit = ???

  override def line(a: Point, b: Point): Unit = ???

  override def triangle(a: Point, b: Point, c: Point): Unit = {} // do nothing

  override def quad(a: Point, b: Point, c: Point, d: Point): Unit = {} // not supported

  override def close(): Unit = {} // do nothing

  override def load(filePath: String): Texture = noTexture // do nothing

  override def use(t: Texture): Unit = texture = t

  override def refresh(): Unit = ???

  override def lines(points: Iterable[Point]): Unit = ???

  override def lineWidth(w: Float): Unit = {} // do nothing

  override def nextEvent(): PlotEvent = PlotEventNone

}

