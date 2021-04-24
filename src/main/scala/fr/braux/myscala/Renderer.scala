package fr.braux.myscala

import fr.braux.myscala.Plotdef._

trait Renderer {
  // properties
  type Texture
  def display: Display

  // actions
  def refresh(): Unit
  def nextEvent(): PlotEvent
  def close(): Unit

  // draw
  def point(a: Point): Unit
  def line(a: Point, b: Point): Unit
  def triangle(a: Point, b: Point, c: Point): Unit
  def quad(a: Point, b: Point, c: Point, d: Point): Unit
  def lines(points: Iterable[Point]): Unit

  // shape
  def color(c: Color): Unit
  def lineWidth(w: Float): Unit

  // texture handling
  def load(filePath: String): Texture
  def use(t: Texture)
}

trait RendererFactory {
  def get: Renderer
}
