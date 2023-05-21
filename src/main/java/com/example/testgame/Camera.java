package com.example.testgame;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private float pitch; // how high or low the camera is aiming
    private float yaw;   // how much left or right the camera is aiming

    public Camera() {
        position = new Vector3f(0, 0, -3);
    }

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float NEAR_PLANE = 0.01f;
    private static final float FAR_PLANE = 1000.0f;

    public Matrix4f getProjectionMatrix() {
        float aspectRatio = (float) 800 / (float) 600;
        return new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(yaw)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(yaw)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(yaw - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(yaw - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        yaw += offsetX;
        pitch += offsetY;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(pitch), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(yaw), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }
}
