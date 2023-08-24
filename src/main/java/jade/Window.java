package jade;

import util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {


    private int width, height;
    private String title;
    private static Window window = null;
    private long glfwWindow;
    private static Scene currentScene;

    public float r, g, b, a;


    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";

        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "unknown scene " + newScene + " ";
                break;
        }
    }

    public static Window get(){
        if (Window.window == null) {
            window = new Window();
        }
        return Window.window;
    }


    public void run() {
        System.out.println("Starting engine on LWJGL " + Version.getVersion() + " !");

        init();
        loop();

        //Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        //Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Init GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Configure
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Create Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if (glfwWindow == NULL) {
            throw  new IllegalStateException("Failed to create the GLFW Window");
        }

        //Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL Context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable v-sync
        glfwSwapInterval(1);

        //Make Window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {

        float beginnTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {

            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginnTime;
            beginnTime = endTime;
        }
    }
}
