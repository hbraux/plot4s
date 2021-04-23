package fr.braux.myscala

import fr.braux.myscala.Plotlib._

trait Renderer {
  type Texture
  def init(): Unit
  def display: Display
  def color(c: Color): Unit
  def point(a: Point): Unit
  def line(a: Point, b: Point): Unit
  def triangle(a: Point, b: Point, c: Point): Unit
  def quad(a: Point, b: Point, c: Point, d: Point): Unit
  def lines(provider: PointProvider): Unit
  def close(): Unit
  def load(filePath: String): Texture
  def use(t: Texture)
  def show(wait: Boolean = true): Unit
}
