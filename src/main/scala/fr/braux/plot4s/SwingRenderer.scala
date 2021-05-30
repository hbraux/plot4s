package fr.braux.plot4s
import fr.braux.plot4s.Plotdef._
import fr.braux.plot4s.SwingRenderer.Painter

import java.awt.event.{KeyEvent, KeyListener}
import java.awt.image.BufferedImage
import java.awt.{BorderLayout, Color, Graphics, Graphics2D}
import java.io.{ByteArrayOutputStream, File}
import javax.imageio.ImageIO
import javax.swing.{JComponent, JFrame, JPanel}
import scala.collection.mutable
import java.awt.event.KeyEvent._

case class SwingRenderer private (title: String, width: Int, height: Int, background: Color, painter: Painter) extends Renderer with KeyListener {

  import SwingRenderer._
  private val eventsQueue = new mutable.Queue[PlotEvent]()

  override type Texture = Array[Byte]

  override def swap(): Unit = {
    painter.refresh()
  }

  override def clear(): Unit = {
    painter.clear()
    painter.repaint()
  }

  override def nextEvent(): PlotEvent = if (eventsQueue.isEmpty) PlotEventNone else eventsQueue.dequeue()

  override def close(): Unit = {
    painter.frame.setVisible(false)
    painter.frame.dispose()
  }

  override def getRaw: Array[Byte] = {
    val os = new ByteArrayOutputStream
    ImageIO.write(painter.image, "PNG", os)
    os.toByteArray
  }

  override def point(p: Point): Unit = painter.add(_.drawLine(p.x, p.y, p.x, p.y))

  override def points(ps: Iterable[Point], joined: Boolean): Unit =
    if (joined) (ps zip ps.tail).foreach(t => line(t._1, t._2)) else ps.foreach(point)

  override def line(from: Point, to: Point): Unit = painter.add(_.drawLine(from.x, from.y, to.x, to.y))

  override def rect(p: Point, w: Int, h: Int): Unit = painter.add(_.fillRect(p.x, p.y, w, h))

  override def useColor(color: Color): Unit =  painter.add(_.setColor(color))

  override def useLine(width: Float): Unit = {} // not supported yet

  override def load(filePath: String): Array[Byte] = throw new NotImplementedError("not supported yet")

  override def useTexture(t: Array[Byte]): Unit = throw new NotImplementedError("not supported yet")

  override def image(img: BufferedImage): Unit = painter.add(_.drawImage(img, 0, 0, painter))

  override def setTitle(title: String): Unit = painter.frame.setTitle(title)

  // key listener
  override def keyTyped(e: KeyEvent): Unit = {}
  override def keyPressed(e: KeyEvent): Unit = {}
  override def keyReleased(e: KeyEvent): Unit = keysMapping.get(e.getKeyCode).foreach(eventsQueue.enqueue(_))
}


object SwingRenderer extends RendererFactory {
  override val defaultSize: Int = 480
  override val defaultBackground: Color = Color.black

  private val keysMapping = Map(
    VK_ESCAPE -> PlotEventEscape,
    VK_SPACE -> PlotEventSpace,
    VK_LEFT -> PlotEventLeft,
    VK_RIGHT -> PlotEventRight,
    VK_UP -> PlotEventUp,
    VK_DOWN -> PlotEventDown,
    VK_PAGE_UP -> PlotEventPageUp,
    VK_PAGE_DOWN -> PlotEventPageDown)


  override def apply(title: String, width: Int, height: Int, background: Color): Renderer = {
    val frame = new JFrame(title) {}
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(width, height)
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val painter = Painter(frame, image, background)
    frame.add(painter)
    frame.setResizable(false)
    frame.setVisible(true)
    val renderer = SwingRenderer(title, width, height, background, painter)
    frame.addKeyListener(renderer)
    renderer
  }

  case class Painter(frame: JFrame, image: BufferedImage, bg: Color) extends JPanel  {
    private val drawStack = mutable.ListBuffer[Graphics => Unit]()

    def clear(): Unit = drawStack.clear()

    def add(f: Graphics => Unit): Unit = {
      drawStack.addOne(f)
    }

    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      g.drawImage(image, 0, 0, null)
    }


    def refresh(): Unit = {
      val graphics = image.createGraphics
      graphics.setBackground(bg)
      if (bg != Color.black) {
        graphics.clearRect(0, 0, image.getWidth, image.getHeight)
      }
      drawStack.foreach(_(graphics))
      graphics.dispose()
      repaint()
    }

  }

}
