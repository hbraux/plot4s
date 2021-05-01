package fr.braux.myscala
import fr.braux.myscala.Plotdef._
import fr.braux.myscala.SwingRenderer.Painter

import java.awt.{BorderLayout, Color, Graphics}
import javax.swing.{JFrame, JPanel}
import scala.collection.mutable

class SwingRenderer private (val width: Int, val height: Int, val background: Color, painter: Painter) extends Renderer {

  override type Texture = Array[Byte]

  override def refresh(): Unit = {
    painter.repaint()
    // painter.clear()
  }

  override def nextEvent(): PlotEvent = PlotEventNone

  override def close(): Unit = {
    painter.frame.setVisible(false)
    painter.frame.dispose()
  }

  override def getRaw: Array[Byte] = throw new NotImplementedError("not supported")

  override def point(p: Point): Unit = painter.add(_.drawLine(p.x, p.y, p.x, p.y))

  override def points(ps: Iterable[Point], joined: Boolean): Unit =
    if (joined) (ps zip ps.tail).foreach(t => line(t._1, t._2)) else ps.foreach(point)

  override def line(from: Point, to: Point): Unit = painter.add(_.drawLine(from.x, from.y, to.x, to.y))

  override def rect(p: Point, w: Int, h: Int): Unit = painter.add(_.fillRect(p.x, p.y, w, h))

  override def pixels(ps: Iterable[(Point, Color)]): Unit = throw new NotImplementedError("not supported yet")

  override def useColor(color: Color): Unit =  painter.add(_.setColor(color))

  override def useLine(width: Float): Unit = throw new NotImplementedError("not supported yet")

  override def load(filePath: String): Array[Byte] = throw new NotImplementedError("not supported yet")

  override def useTexture(t: Array[Byte]): Unit = throw new NotImplementedError("not supported yet")
}


object SwingRenderer extends RendererFactory {
  override val defaultSize: Int = 840
  override val defaultBackground: Color = Color.white


  override def apply(title: String, width: Int, height: Int, background: Color): Renderer = {
    val frame = new JFrame(title) {}
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(width, height)
    val painter = new Painter(frame)
    frame.add(painter, BorderLayout.CENTER)
    frame.setVisible(true)
    new SwingRenderer(width, height, background, painter)
  }

  class Painter(val frame: JFrame) extends JPanel  {
    private val drawStack = mutable.Queue[Graphics => Unit]()

    def clear(): Unit = drawStack.clear()

    def add(f: Graphics => Unit): Unit = {
      drawStack.enqueue(f)
    }

    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      while (drawStack.nonEmpty)  drawStack.dequeue()(g)
    }
  }

}
