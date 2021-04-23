package fr.braux.myscala


import fr.braux.myscala.Plotlib._

class ConsoleRenderer private extends Renderer {

  override type Texture = Char

  override def init(): Unit = ???

  override def color(c: Color): Unit = ???

  override def point(a: Point): Unit = ???

  override def line(a: Point, b: Point): Unit = ???

  override def triangle(a: Point, b: Point, c: Point): Unit = ???

  override def quad(a: Point, b: Point, c: Point, d: Point): Unit = ???

  override def close(): Unit = ???

  override def load(filePath: String): Texture = ???

  override def use(t: Texture): Unit = ???

  override def show(wait: Boolean): Unit = ???

  override def display: Display = ???

  override def lines(provider: PointProvider): Unit = ???
}

object ConsoleRenderer {
  def apply() = new ConsoleRenderer()
}