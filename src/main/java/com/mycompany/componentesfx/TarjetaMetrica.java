package com.mycompany.componentesfx;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TarjetaMetrica extends VBox {

    private final Label valor;

    public TarjetaMetrica(String titulo, String valorInicial) {
        Label cabecera = new Label(titulo);
        cabecera.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");

        valor = new Label(valorInicial);
        valor.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-background-radius: 14; -fx-border-radius: 14;");
        getChildren().addAll(cabecera, valor);
    }

    public void setValor(String texto) {
        valor.setText(texto);
    }
}
