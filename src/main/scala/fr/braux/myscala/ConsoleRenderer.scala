package fr.braux.myscala


import fr.braux.myscala.Plotdef._

class ConsoleRenderer private extends Renderer {

  override type Texture = Char

  override def color(c: Color): Unit = ???

  override def point(a: Point): Unit = ???

  override def line(a: Point, b: Point): Unit = ???

  override def triangle(a: Point, b: Point, c: Point): Unit = {} // not supported

  override def quad(a: Point, b: Point, c: Point, d: Point): Unit = {} // not supported

  override def close(): Unit = ???

  override def load(filePath: String): Texture = ???

  override def use(t: Texture): Unit = ???

  override def display: Display = ???

  override def refresh(): Unit = ???

  override def lines(points: Iterable[Point]): Unit = ???

  override def lineWidth(w: Float): Unit = {} // not supported

  override def nextEvent(): PlotEvent = ???
}

object ConsoleRenderer {

  def apply(settings: PlotSettings): Renderer = new ConsoleRenderer()
}