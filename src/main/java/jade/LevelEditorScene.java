package jade;

import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout(location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";
    
    private int vertexID, fragmentID, shaderProgramm;

    private float[] vertexArray = {
        //positions                  //color
        0.5f,   -0.5f,  0.0f,       1.0f, 0.0f, 0.0f, 1.0f,     //Bottom right
        -0.5f,  0.5f,   0.0f,       0.0f, 1.0f, 0.0f, 1.0f,     //Top left
        0.5f,   0.5f,   0.0f,       0.0f, 0.0f, 1.0f, 1.0f,     //Top right
        -0.5f,  -0.5f,  0.0f,       1.0f, 1.0f, 0.0f, 1.0f      //Bottom left
    };

    //COUNTER-CLOCKWISE-ORDER !!!
    private int[] elementArray = {
        2, 1, 0, //Top right
        0, 1, 3 //bottom rigth
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("Inside LevelEditorScene");
    }

    @Override
    public void init() {
        //Compile and Link Shaders
        //Load and compile Vertex
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        //Pass the Shader to the GPU
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        //Check for Error
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: vertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false: "";
        }

        //Load and compile Fragment
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        //Pass the Shader to the GPU
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        //Check for Error
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: fragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false: "";
        }

        //Link Shaders and check for errors
        shaderProgramm = glCreateProgram();
        glAttachShader(shaderProgramm, vertexID);
        glAttachShader(shaderProgramm, fragmentID);
        glLinkProgram(shaderProgramm);

        //check for Linking Errors
        success = glGetProgrami(shaderProgramm, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramm, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: linking shaders failed");
            System.out.println(glGetProgramInfoLog(fragmentID, len));
            assert false: "";
        }

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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        //Bind shader programm
        glUseProgram(shaderProgramm);
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

        glUseProgram(0);
    }
}
