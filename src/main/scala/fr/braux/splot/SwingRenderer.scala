package fr.braux.splot
import fr.braux.splot.Plotdef._
import fr.braux.splot.SwingRenderer.Painter

import java.awt.image.BufferedImage
import java.awt.{BorderLayout, Color, Graphics, Graphics2D}
import java.io.{ByteArrayOutputStream, File}
import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}
import scala.collection.mutable

case class SwingRenderer private (title: String, width: Int, height: Int, background: Color, painter: Painter) extends Renderer {

  override type Texture = Array[Byte]

  override def swap(): Unit = {
    painter.refresh()
  }

  override def clear(): Unit = {
    painter.clear()
    painter.repaint()
  }

  override def nextEvent(): PlotEvent = PlotEventNone

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
}


object SwingRenderer extends RendererFactory {
  override val defaultSize: Int = 240
  override val defaultBackground: Color = Color.black


  override def apply(title: String, width: Int, height: Int, background: Color): Renderer = {
    val frame = new JFrame(title) {}
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(width, height)
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val painter = Painter(frame, image, background)
    frame.add(painter)
    frame.setResizable(false)
    frame.setVisible(true)
    new SwingRenderer(title, width, height, background, painter)
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
