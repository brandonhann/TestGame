package com.example.testgame;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

public class Game {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private long window;
    private Shader shader;
    private Camera camera;
    private Mesh mesh;
    private float time;

    public void run() throws Exception {
        init();
        loop();
        cleanup();
    }

    private void init() throws Exception {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Set up GLFW window properties
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "My Game", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);
        // Enable v-sync
        GLFW.glfwSwapInterval(1);
        // Make the window visible
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Create the shader
        shader = new Shader("/com/example/testgame/vertex.vs", "/com/example/testgame/fragments.fs");

        // Set up the vertices for the cube
        float[] vertices = new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };

        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 5, 5, 0, 3,
                // Right face
                3, 2, 5, 5, 2, 7,
                // Left face
                4, 6, 0, 0, 6, 1,
                // Bottom face
                1, 6, 2, 2, 6, 7,
                // Back face
                4, 5, 6, 6, 5, 7,
        };

        mesh = new Mesh(vertices, indices);
        camera = new Camera();
    }

    private void loop() {
        double lastFrameTime = GLFW.glfwGetTime();
        while (!GLFW.glfwWindowShouldClose(window)) {
            double currentTime = GLFW.glfwGetTime();
            float deltaTime = (float) (currentTime - lastFrameTime);
            lastFrameTime = currentTime;
            time += deltaTime;

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            shader.bind();

            // Update the projection Matrix
            Matrix4f projectionMatrix = camera.getProjectionMatrix();
            shader.setUniform("projection", projectionMatrix);

            // Update the view Matrix
            Matrix4f viewMatrix = camera.getViewMatrix();
            shader.setUniform("view", viewMatrix);

            render();

            shader.unbind();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void render() {
        // Transform the mesh
        float angle = time * 50.0f; // The cube will rotate at 50 degrees per second
        Matrix4f modelMatrix = new Matrix4f().identity().rotate((float) Math.toRadians(angle), 1, 1, 0);

        // Update the model Matrix
        shader.setUniform("model", modelMatrix);

        // Draw the mesh
        GL30.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // Restore state
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


    private void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
        if (mesh != null) {
            mesh.cleanup();
        }
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        Game game = new Game();
        try {
            game.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
