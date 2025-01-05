module com.example.ridebuddies {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;


    opens org.example.ridebuddies.model to javafx.base;
    exports org.example.ridebuddies.model;

    opens org.example.ridebuddies.repository to java.sql;
    exports org.example.ridebuddies.repository;

    exports org.example.ridebuddies;

    opens org.example.ridebuddies.connection to java.sql, javafx.fxml;
    exports org.example.ridebuddies.connection;
    opens org.example.ridebuddies to java.sql, javafx.fxml;

}