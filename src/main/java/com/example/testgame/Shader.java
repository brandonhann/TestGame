package com.example.testgame;

import org.lwjgl.opengl.GL20;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public class Shader {

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    private final int programId;

    public Shader() throws Exception {
        this("/com/example/testgame/vertex.vs", "/com/example/testgame/fragment.fs");
    }


    public Shader(String vertexShaderFile, String fragmentShaderFile) throws Exception {
        programId = GL20.glCreateProgram();

        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }

        int vertexShaderId = createShader(vertexShaderFile, GL20.GL_VERTEX_SHADER);
        int fragmentShaderId = createShader(fragmentShaderFile, GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);

        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }
    public void setUniform(String name, Matrix4f value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if(location != -1) {
            matrixBuffer.clear(); // Clear the existing buffer data
            value.get(matrixBuffer); // Fill the buffer with the matrix data
            GL20.glUniformMatrix4fv(location, false, matrixBuffer);
        } else {
            System.out.println("Warning: could not find uniform variable '" + name + "'");
        }
    }


    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }

    private int createShader(String filename, int shaderType) throws Exception {
        StringBuilder shaderSource = new StringBuilder();

        try (InputStream in = Shader.class.getResourceAsStream(filename)) {
            if (in == null) {
                throw new Exception("Could not find file: " + filename);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    shaderSource.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            throw new Exception("Could not read file.", e);
        }

        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderSource);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }

        return shaderId;
    }
}
