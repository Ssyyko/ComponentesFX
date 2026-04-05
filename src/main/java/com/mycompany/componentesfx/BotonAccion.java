package com.mycompany.componentesfx;

import javafx.scene.control.Button;

public class BotonAccion extends Button {

    public BotonAccion(String texto, String colorFondo) {
        super(texto);
        setStyle("-fx-background-color: " + colorFondo + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 12;");
        setPrefHeight(40);
        setMaxWidth(Double.MAX_VALUE);
    }
}
