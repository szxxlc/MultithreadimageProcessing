module dev.paulina.multithreadimageprocessing {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens dev.paulina.multithreadimageprocessing to javafx.fxml;
    exports dev.paulina.multithreadimageprocessing;
}