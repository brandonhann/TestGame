module com.example.testgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.joml;


    opens com.example.testgame to javafx.fxml;
    exports com.example.testgame;
}