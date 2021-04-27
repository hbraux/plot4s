package fr.braux.myscala

import fr.braux.myscala.Plotdef._

trait Renderer {
  // properties
  type Texture

  def supportEvents: Boolean
  def width: Int
  def height: Int
  def minPoint: Point
  def maxPoint: Point

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

  def dx: Float = maxPoint.x - minPoint.x
  def dy: Float = maxPoint.y - minPoint.y
  def dz: Float = maxPoint.z - minPoint.z
  def xmin: Float = minPoint.x
  def ymin: Float = minPoint.y
  def zmin: Float = minPoint.z
}
