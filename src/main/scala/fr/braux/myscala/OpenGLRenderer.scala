package fr.braux.myscala


import fr.braux.myscala.Plotlib._
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.slf4j.LoggerFactory

import scala.collection.mutable

class OpenGLRenderer private (val display: Display, window: Long) extends Renderer {
  import OpenGLRenderer._

  override type Texture = GlTexture

  private val keysQueue = new mutable.Queue[PlotConst]()

  // window callback enqueue Key released events
  glfwSetKeyCallback(window, (w, key, _, action, _) => (action, keyMappings.get(key)) match {
    case (GLFW_RELEASE, Some(k)) => keysQueue.enqueue(k)
    case _ =>
  })

  override def close(): Unit = {
      glfwFreeCallbacks(window)
      glfwDestroyWindow(window)
      glfwTerminate()
      glfwSetErrorCallback(null).free()
    }

  override def refresh(): Unit = glfwSwapBuffers(window)


  override def nextKeyEvent(): Option[PlotConst] = {
    glfwPollEvents()
    if (keysQueue.isEmpty) None else Some(keysQueue.dequeue())
  }

  override def color(c: Color): Unit = {
    glClear(GL_COLOR_BUFFER_BIT)
    glColor3f(c.red, c.green, c.blue)
  }

  override def point(a: Point): Unit = {
    glBegin(GL_POINT)
    glVertex3f(a.x, a.y, a.z)
    glEnd()
  }

  override def line(a: Point, b: Point): Unit =  {
    glBegin(GL_LINE)
    glVertex3f(a.x, a.y, a.z)
    glVertex3f(b.x, b.y, b.z)
    glEnd()
  }

  override def triangle(a: Point, b: Point, c: Point): Unit = {
    glBegin(GL_TRIANGLES)
    glVertex3f(a.x, a.y, a.z)
    glVertex3f(b.x, b.y, b.z)
    glVertex3f(c.x, c.y, c.z)
    glEnd()
  }

  override def quad(a: Point, b: Point, c: Point, d: Point): Unit = {
    glBegin(GL_QUADS)
    glVertex3f(a.x, a.y, a.z)
    glVertex3f(b.x, b.y, b.z)
    glVertex3f(c.x, c.y, c.z)
    glVertex3f(d.x, d.y, d.z)
    glEnd()
  }

  override def lines(points: Iterable[Point]): Unit = {
    glBegin(GL_LINE_STRIP)
    points.foreach(p => glVertex3f(p.x, p.y, p.z))
    glEnd()
  }

  override def load(filePath: String): GlTexture = ???

  override def use(t: GlTexture): Unit = ???

}

object OpenGLRenderer  {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  private val keyMappings: Map[Int, PlotConst] = Map(GLFW_KEY_ESCAPE -> PlotKeyEscape, GLFW_KEY_SPACE -> PlotKeySpace)

  def apply(settings: PlotSettings): Renderer = {
    if (!glfwInit)
      throw new IllegalStateException("Unable to initialize GLFW")
    glfwSetErrorCallback(new GLFWErrorCallback() {
      override def invoke(error: Int, description: Long): Unit = logger.error(s"OpenGL error $error/$description")
    })
    val display = Display(settings.get(PlotWindowWidth,300), settings.get(PlotWindowWidth,300), Point(-1, -1, -1), Point(1, 1, 1))
    val window = Option(glfwCreateWindow(display.width, display.height, settings.get(PlotWindowTitle,"title"), 0, 0)).
      getOrElse(throw new IllegalStateException("Unable to create window"))
    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    GL.createCapabilities()
    glfwShowWindow(window)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    new OpenGLRenderer(display, window)
  }


  class GlTexture()
}
