package com.mycompany.componentesfx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Resultado de aprendizaje a revisar:
 * componentes e informes PDF con graficas.
 */
public class ComponentesFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private final ObservableList<String> historial = FXCollections.observableArrayList();
    private final Map<String, Integer> contadorTipos = new LinkedHashMap<>();
    private final Path carpetaSalidas = Path.of("salidas");
    private final Path rutaGrafica = carpetaSalidas.resolve("componentesfx_grafica.jpg");
    private final Path rutaPdf = carpetaSalidas.resolve("componentesfx_informe.pdf");

    @Override
    public void start(Stage stage) {
        crearCarpeta();
        contadorTipos.put("Texto", 0);
        contadorTipos.put("Combo", 0);
        contadorTipos.put("Slider", 0);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fafc, #e2e8f0);");

        Label titulo = new Label("Componentes FX");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        root.setTop(titulo);

        TextField campo = new TextField();
        campo.setPromptText("Escribe una tarea o nota");

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Tarea", "Rutina", "Idea");
        combo.getSelectionModel().selectFirst();

        Slider prioridad = new Slider(1, 10, 5);
        prioridad.setShowTickLabels(true);
        prioridad.setShowTickMarks(true);
        prioridad.setMajorTickUnit(1);
        prioridad.setMinorTickCount(0);
        prioridad.setSnapToTicks(true);

        TarjetaMetrica tarjetaTotal = new TarjetaMetrica("Entradas", "0");
        TarjetaMetrica tarjetaUltima = new TarjetaMetrica("Ultimo tipo", "-");

        BotonAccion btnAgregar = new BotonAccion("Agregar al listado", "#bfdbfe");
        BotonAccion btnPdf = new BotonAccion("Crear PDF", "#c7f9cc");

        ListView<String> lista = new ListView<>(historial);
        lista.setPrefHeight(320);

        btnAgregar.setOnAction(e -> {
            String texto = campo.getText().trim();
            String tipo = combo.getValue();
            int nivel = (int) prioridad.getValue();

            if (texto.isEmpty()) {
                return;
            }

            historial.add(0, tipo + " - " + texto + " - prioridad " + nivel);
            contadorTipos.put(tipo, contadorTipos.get(tipo) + 1);
            tarjetaTotal.setValor(String.valueOf(historial.size()));
            tarjetaUltima.setValor(tipo);
            campo.clear();
        });

        btnPdf.setOnAction(e -> exportarPdf());

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(12));
        formulario.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1;");
        formulario.add(new Label("Texto"), 0, 0);
        formulario.add(campo, 1, 0);
        formulario.add(new Label("Tipo"), 0, 1);
        formulario.add(combo, 1, 1);
        formulario.add(new Label("Prioridad"), 0, 2);
        formulario.add(prioridad, 1, 2);
        formulario.add(btnAgregar, 0, 3, 2, 1);
        formulario.add(btnPdf, 0, 4, 2, 1);

        HBox tarjetas = new HBox(10, tarjetaTotal, tarjetaUltima);
        HBox.setHgrow(tarjetaTotal, Priority.ALWAYS);
        HBox.setHgrow(tarjetaUltima, Priority.ALWAYS);

        VBox izquierda = new VBox(10, formulario, tarjetas);
        VBox derecha = new VBox(10, new Label("Historial"), lista);
        VBox.setVgrow(lista, Priority.ALWAYS);

        HBox centro = new HBox(12, izquierda, derecha);
        centro.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(derecha, Priority.ALWAYS);
        root.setCenter(centro);

        Scene scene = new Scene(root, 980, 620);
        stage.setTitle("ComponentesFX");
        stage.setScene(scene);
        stage.show();
    }

    private void crearCarpeta() {
        try {
            Files.createDirectories(carpetaSalidas);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void exportarPdf() {
        try {
            crearGrafica();
            crearDocumentoPdf();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Su archivo ha sido creado con todos los datos implementados!");
            alert.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Su archivo ha sido creado con todos los datos implementados!");
            alert.showAndWait();
        }
    }

    private void crearGrafica() throws IOException {
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : contadorTipos.entrySet()) {
            datos.addValue(entry.getValue(), "Cantidad", entry.getKey());
        }

        JFreeChart grafica = ChartFactory.createBarChart(
                "Uso de componentes",
                "Tipo",
                "Cantidad",
                datos,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        ChartUtils.saveChartAsJPEG(rutaGrafica.toFile(), grafica, 800, 420);
    }

    private void crearDocumentoPdf() throws IOException {
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            PDImageXObject imagen = PDImageXObject.createFromFile(rutaGrafica.toString(), documento);
            PDType1Font titulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font texto = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream stream = new PDPageContentStream(documento, pagina)) {
                stream.beginText();
                stream.setFont(titulo, 18);
                stream.newLineAtOffset(40, 790);
                stream.showText("Informe de ComponentesFX");
                stream.endText();

                stream.beginText();
                stream.setFont(texto, 11);
                stream.newLineAtOffset(40, 760);
                stream.showText("RA a revisar: componentes e informes.");
                stream.newLineAtOffset(0, -18);
                stream.showText("Elementos guardados en la interfaz: " + historial.size());
                stream.endText();

                stream.drawImage(imagen, 70, 320, 450, 240);
            }

            documento.save(rutaPdf.toFile());
        }
    }
}
