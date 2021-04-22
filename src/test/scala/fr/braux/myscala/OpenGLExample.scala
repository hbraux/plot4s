package fr.braux.myscala

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._

object OpenGLExample extends App {

  // https://gist.github.com/superblaubeere27/c7a935f672e8567c79dcd7da71f54bd3
  private var sp = 0.0f
  private var swapcolor = false

  System.out.println("Hello LWJGL " + Version.getVersion + "!")
  // will print the error message in System.err.
  GLFWErrorCallback.createPrint(System.err).set()

  // Initialize GLFW. Most GLFW functions will not work before doing this.
  if (!glfwInit) throw new IllegalStateException("Unable to initialize GLFW")

  // Configure GLFW
  glfwDefaultWindowHints // optional, the current window hints are already the default

  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation

  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

  // Create the window
  val windowWidth = 300
  val windowHeight = 300
  val window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", 0, 0)
  if (window == null)
    throw new RuntimeException("Failed to create the GLFW window")


  // Setup a key callback. It will be called every time a key is pressed, repeated or released.
  glfwSetKeyCallback(window, (w, key, scancode, action, mods) => {
    println(s"callback key=$key action=$action")
    if ((key == GLFW_KEY_ESCAPE) && (action == GLFW_RELEASE))
      glfwSetWindowShouldClose(w, true) // We will detect this in the rendering loop
  }
  )


  val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor)
  // glfwSetWindowPos(window, (vidmode.width - width) / 2, (vidmode.height - height) / 2)


  // Make the OpenGL context current
  glfwMakeContextCurrent(window)

  // Enable v-sync
  glfwSwapInterval(1)
  // Make the window visible
  glfwShowWindow(window)

  // OpenGL context, or any context that is managed externally.
  // LWJGL detects the context that is current in the current thread,
  // creates the GLCapabilities instance and makes the OpenGL
  // bindings available for use.
  GL.createCapabilities()
  System.out.println("OpenGL Version : " + glGetString(GL_VERSION))
  System.out.println("OpenGL Renderer : " + glGetString(GL_RENDERER))
  // System.out.println("OpenGL Extensions supported: " + glGetString(GL_EXTENSIONS).split("\\ ").mkString(","))

  glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
  // 2D stuff
  glMatrixMode(GL_PROJECTION)
  glLoadIdentity
  // glOrtho(0.0f, windowWidth, windowHeight, 0.0f, 0.0f, 1.0f)

  // Run the rendering loop until the user has attempted to close
  // the window or has pressed the ESCAPE key.
  var running = true
  while (running) {

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer
    // drawSquare(10)
    drawCurve
    glfwSwapBuffers(window) // swap the color buffers
    // Poll for window events. The key callback above will only be invoked during this call.
    glfwPollEvents
    if (glfwWindowShouldClose(this.window))
      running = false
  }
  // Free the window callbacks and destroy the window
  glfwFreeCallbacks(window)
  glfwDestroyWindow(window)
  // Terminate GLFW and free the error callback
  glfwTerminate
  glfwSetErrorCallback(null).free()


  private def drawQuad(): Unit = {
    sp = sp + 0.001f
    if (sp > 1.0f) {
      sp = 0.0f
      swapcolor = !swapcolor
    }
    if (!swapcolor) glColor3f(0.0f, 1.0f, 0.0f)
    else glColor3f(0.0f, 0.0f, 1.0f)

    glBegin(GL_QUADS)
    glVertex2f(-sp, -sp)
    glVertex2f(sp, -sp)
    glVertex2f(sp, sp)
    glVertex2f(-sp, sp)
    glEnd()
  }

  def drawCurve(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT)
    glColor3f(1.0f, 0.0f, 0.0f)
    glLineWidth(2.0f)
    val f: Double => Double = (x: Double) => Math.sin(x * 2)
    glBegin(GL_LINE_STRIP)
    (0 to windowWidth).foreach { i =>
      val x = (i - windowWidth / 2) / 100f
      val y = f(x).toFloat
      glVertex2f(x, y)
    }
    glEnd
  }

  def drawSquare(size: Int): Unit = {
    glClear(GL_COLOR_BUFFER_BIT)
    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
    glColor3f(1.0f, 0.0f, 0.0f)
    val w: Float = (windowHeight min windowWidth) / size
    for (xi <- 0 until size) {
      for (yi <- 0 until size) {
        glBegin(GL_POLYGON)
        glVertex2f(xi * w, yi * w)
        glVertex2f((xi + 1) * w, yi * w)
        glVertex2f((xi + 1) * w, (yi + 1) * w)
        glVertex2f(xi * w, (yi + 1) * w)
        glEnd
      }
    }
    glFlush
  }

}
