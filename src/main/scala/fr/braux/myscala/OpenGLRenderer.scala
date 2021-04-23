package fr.braux.myscala


import fr.braux.myscala.Plotlib._
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.slf4j.LoggerFactory

class OpenGLRenderer private extends Renderer {
  import OpenGLRenderer._

  override type Texture = GlTexture

  private var window: Long = 0L
  private var refresh = true
  private var render: () => Unit = () => {}
  private var _display = Display(400, 400, Point(-1, -1, -1), Point(1, 1, 1))

  override def display: Display = _display

  override def init(): Unit = {
    assert(isReady)
    window = Option(glfwCreateWindow(display.width, display.height, "title", 0, 0)).
      getOrElse(throw new IllegalStateException("Unable to create window"))

    glfwSetKeyCallback(window, (w, key, _, action, _) => (key, action) match {
      case (GLFW_KEY_ESCAPE, GLFW_RELEASE) => glfwSetWindowShouldClose(w, true)
      case (GLFW_KEY_Z, GLFW_RELEASE) => zoom(0.5f)
      case _ =>
    })

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    GL.createCapabilities()
    glfwShowWindow(window)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
  }

  private def zoom(factor: Float): Unit = {
    _display = display.zoom(factor)
    render()
    refresh = true
  }

  override def close(): Unit = {
    if (window > 0) {
      glfwFreeCallbacks(window)
      glfwDestroyWindow(window)
      glfwTerminate()
      glfwSetErrorCallback(null).free()
    }
    window = 0L
  }

  override def show(wait: Boolean = true): Unit = {
    var running = wait
    while (running) {
      if (refresh) {
        glfwSwapBuffers(window)
        refresh = false
      }
      glfwPollEvents()
      if (glfwWindowShouldClose(this.window))
        running = false
    }
  }

  override def color(c: Color): Unit = {
    glClear(GL_COLOR_BUFFER_BIT)
    glColor3f(c.red, c.green, c.blue)
  }

  override def point(a: Point): Unit = ???

  override def line(a: Point, b: Point): Unit = ???

  override def triangle(a: Point, b: Point, c: Point): Unit = ???

  override def quad(a: Point, b: Point, c: Point, d: Point): Unit = ???

  override def lines(provider: PointProvider): Unit = {
    render = () => {
      glBegin(GL_LINE_STRIP)
      provider.getPoints(display).foreach(p => glVertex3f(p.x, p.y, p.z))
      glEnd()
    }
    render()
  }

  override def load(filePath: String): GlTexture = ???

  override def use(t: GlTexture): Unit = ???


}

object OpenGLRenderer {
  def apply() = new OpenGLRenderer()

  private lazy val logger = LoggerFactory.getLogger(getClass)

  private lazy val isReady = {
    if (!glfwInit)
      throw new IllegalStateException("Unable to initialize GLFW")
    glfwSetErrorCallback(new GLFWErrorCallback() {
      override def invoke(error: Int, description: Long): Unit = logger.error(s"OpenGL error $error/$description")
    })
    true
  }


  class GlTexture()
}