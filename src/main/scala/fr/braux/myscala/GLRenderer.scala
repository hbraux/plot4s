package fr.braux.myscala


import fr.braux.myscala.Plot.Setting._
import fr.braux.myscala.Plot._
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.slf4j.LoggerFactory

class GLRenderer private extends Renderer {
  import GLRenderer._

  override type Texture = GlTexture

  private var window: Long = 0L

  override def init(title: String, params: Map[Setting.Value, Int] =  Map.empty): Unit = {
    assert(isReady)
    window = Option(glfwCreateWindow(params.getOrElse(Width, 400), params.getOrElse(Height, 400), title, 0, 0)).
      getOrElse(throw new IllegalStateException("Unable to create window"))

    glfwSetKeyCallback(window, (w, key, _, action, _) =>
      if ((key == GLFW_KEY_ESCAPE) && (action == GLFW_RELEASE))
        glfwSetWindowShouldClose(w, true))

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    GL.createCapabilities()
    glfwShowWindow(window)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
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
    glfwSwapBuffers(window)
    while (running) {
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

  override def lines(points: Iterable[Point]): Unit = {
    glBegin(GL_LINE_STRIP)
    points.foreach(p => glVertex3f(p.x, p.y, p.z))
    glEnd()
  }

  override def load(filePath: String): GlTexture = ???

  override def use(t: GlTexture): Unit = ???

  override val corners: (Point, Point) = (Point(-1,- 1,-1), Point(1, 1, 1))


}

object GLRenderer {
  def apply() = new GLRenderer()

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