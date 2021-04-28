package fr.braux.myscala


import fr.braux.myscala.Plotdef._
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.slf4j.LoggerFactory

import scala.collection.mutable

class OpenGLRenderer private (val width: Int, val height: Int, val background: Color, window: Long) extends Renderer {
  import OpenGLRenderer._

  override type Texture = GlTexture

  override val minPoint: Point = Point(-1,-1,-1)
  override val maxPoint: Point = Point(1,1,1)

  private val eventsQueue = new mutable.Queue[PlotEvent]()

  glfwSetKeyCallback(window, (w, key, _, action, _) => (action, keysMapping.get(key)) match {
    case (GLFW_RELEASE, Some(k)) => eventsQueue.enqueue(k)
    case _ =>
  })

  override def close(): Unit = {
      glfwFreeCallbacks(window)
      glfwDestroyWindow(window)
      glfwTerminate()
      glfwSetErrorCallback(null).free()
    }

  override def refresh(): Unit = {
    glfwSwapBuffers(window)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
  }

  override def nextEvent(): PlotEvent = {
    glfwPollEvents()
    if (eventsQueue.isEmpty) PlotEventNone else eventsQueue.dequeue()
  }

  override def useColor(c: Color): Unit = {
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

  override def useLine(width: Float): Unit =  glLineWidth(width)

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

  override def load(filePath: String): GlTexture = new GlTexture()

  override def useTexture(t: GlTexture): Unit = {}

  override def asBinary(): Array[Byte] = "".getBytes()
}

object OpenGLRenderer  {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  private val keysMapping = Map(
    GLFW_KEY_ESCAPE -> PlotEventEscape,
    GLFW_KEY_SPACE -> PlotEventSpace,
    GLFW_KEY_LEFT -> PlotEventSpace,
    GLFW_KEY_RIGHT -> PlotEventRight,
    GLFW_KEY_UP -> PlotEventUp,
    GLFW_KEY_DOWN -> PlotEventDown,
    GLFW_KEY_PAGE_UP -> PlotEventPageUp,
    GLFW_KEY_PAGE_DOWN -> PlotEventPageDown)

  def apply(width: Int, height: Int, title: String, background: Color): Renderer = {
    if (!glfwInit)
      throw new IllegalStateException("Unable to initialize GLFW")
    glfwSetErrorCallback(new GLFWErrorCallback() {
      override def invoke(error: Int, description: Long): Unit = logger.error(s"OpenGL error $error/$description")
    })
    val window = Option(glfwCreateWindow(width, height, title, 0, 0)).
      getOrElse(throw new IllegalStateException("Unable to create window"))
    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    GL.createCapabilities()
    glfwShowWindow(window)
    glClearColor(background.red, background.green, background.blue, 0.0f)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    new OpenGLRenderer(width, height, background, window)
  }

  class GlTexture()
}
