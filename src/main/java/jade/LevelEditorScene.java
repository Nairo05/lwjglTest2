package jade;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private float[] vertexArray = {
        //positions                         color                       UV Coordinates
        100.5f,     0.5f,       0.0f,       1.0f, 0.0f, 0.0f, 1.0f,     1, 1,   //Bottom right
        0.5f,       100.5f,     0.0f,       0.0f, 1.0f, 0.0f, 1.0f,     0, 0,   //Top left
        100.5f,     100.5f,     0.0f,       0.0f, 0.0f, 1.0f, 1.0f,     1, 0,   //Top right
        0.5f,       0.5f,       0.0f,       1.0f, 1.0f, 0.0f, 1.0f,     0, 1    //Bottom left
    };

    //COUNTER-CLOCKWISE-ORDER !!!
    private int[] elementArray = {
        2, 1, 0, //Top right
        0, 1, 3 //bottom rigth
    };

    private boolean firstTime = true;

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;
    GameObject testObj;

    public LevelEditorScene() {
        System.out.println("Inside LevelEditorScene");
    }

    @Override
    public void init() {
        this.testObj = new GameObject("test");
        this.testObj.addComponent(new SpriteRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f());

        //Compile and Link Shaders
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();

        this.testTexture = new Texture("assets/images/testImage.jpg");

        //generate VAO VBO AND EBO buffer Objects and send to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Create VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //Create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        //Upload Texture to Shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionsMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        //Bind VAO
        glBindVertexArray(vaoID);
        //enable vertexAttributePointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();

        if (firstTime) {
            System.out.println("creating Object");
            GameObject go = new GameObject("tst 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = false;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }
}
