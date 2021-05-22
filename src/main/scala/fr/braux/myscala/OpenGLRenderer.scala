package fr.braux.myscala


import fr.braux.myscala.Plotdef._
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.slf4j.LoggerFactory

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable

class OpenGLRenderer private (val width: Int, val height: Int, val background: Color, window: Long) extends Renderer {
  import OpenGLRenderer._

  override type Texture = GlTexture

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


  override def clear(): Unit = glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

  override def swap(): Unit = glfwSwapBuffers(window)

  override def nextEvent(): PlotEvent = {
    glfwPollEvents()
    if (eventsQueue.isEmpty) PlotEventNone else eventsQueue.dequeue()
  }

  override def useColor(color: Color): Unit = glColor3f(color.getRed/255f, color.getGreen/255f, color.getBlue/255f)

  override def point(p: Point): Unit = {
    glBegin(GL_POINT)
    glVertex3f(p.x, height - p.y, 0f)
    glEnd()
  }

  override def line(from: Point, to: Point): Unit =  {
    glBegin(GL_LINE)
    glVertex3f(from.x, height - from.y, 0f)
    glVertex3f(to.x, height - to.y, 0f)
    glEnd()
  }

  override def points(ps: Iterable[Point], joined: Boolean): Unit = {
    if (joined) glBegin(GL_LINE_STRIP) else glBegin(GL_POINT)
    ps.foreach(p => glVertex3f(p.x, height - p.y, 0f))
    glEnd()
  }

  override def useLine(width: Float): Unit =  glLineWidth(width)


  override def rect(p: Point, w: Int, h: Int): Unit = {
    glBegin(GL_QUADS)
    glVertex3f(p.x, height - p.y, 0f)
    glVertex3f(p.x + w, height - p.y, 0f)
    glVertex3f(p.x + w, height - (p.y + h), 0f)
    glVertex3f(p.x, height - (p.y + h), 0f)
    glEnd()
  }

  override def load(filePath: String): GlTexture = new GlTexture()

  override def useTexture(t: GlTexture): Unit = {}

  override def getRaw: Array[Byte] = throw new NotImplementedError("not supported")

  override def image(img: BufferedImage): Unit =  throw new NotImplementedError("not supported")
}

object OpenGLRenderer extends RendererFactory {
  override val defaultSize: Int = 840 // multiple of 2,3,5,7,8
  override val defaultBackground: Color = Color.white

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

  override def apply(title: String, width: Int, height: Int,  background: Color): Renderer = {
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
    // 2D projection
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, width, height, 0, 1, -1)
    glMatrixMode(GL_MODELVIEW)
    glfwShowWindow(window)
    glClearColor(background.getRed/255f, background.getGreen/255f, background.getBlue/255f, 0.0f)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    new OpenGLRenderer(width, height, background, window)
  }

  class GlTexture()
}


