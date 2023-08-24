package renderer;

import org.lwjgl.system.CallbackI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private final String defaultVertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout(location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private final String defaultFragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";


    private int shaderProgrammID;
    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filePath) {
        this.filePath = filePath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equalsIgnoreCase("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equalsIgnoreCase("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token" + firstPattern);
            }

            if (secondPattern.equalsIgnoreCase("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equalsIgnoreCase("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token" + firstPattern);
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false: "Could not open file for shader " + filePath;
        }
    }

    public void compileAndLink() {

        int vertexID, fragmentID;

        //Load and compile Vertex
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        //Pass the Shader to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //Check for Error
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: vertex shader " + filePath + " compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false: "";
        }

        //Load and compile Fragment
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        //Pass the Shader to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //Check for Error
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: fragment shader " + filePath + " compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false: "";
        }

        //Link Shaders and check for errors
        shaderProgrammID = glCreateProgram();
        glAttachShader(shaderProgrammID, vertexID);
        glAttachShader(shaderProgrammID, fragmentID);
        glLinkProgram(shaderProgrammID);

        //check for Linking Errors
        success = glGetProgrami(shaderProgrammID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgrammID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: linking " + filePath + " shaders failed");
            System.out.println(glGetProgramInfoLog(fragmentID, len));
            assert false: "";
        }
    }

    public void use() {
        //Bind shader programm
        glUseProgram(shaderProgrammID);
    }

    public void detach() {
        glUseProgram(0);
    }

}
