package vista.views;

import modelo.Pista;
import servicio.ClubDeportivo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PistaFormView extends GridPane {

    public PistaFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        TextField id = new TextField();
        TextField deporte = new TextField();
        TextField descripcion = new TextField();
        CheckBox disponible = new CheckBox("Disponible");
        Button crear = new Button("Crear");

        addRow(0, new Label("idPista*"), id);
        addRow(1, new Label("Deporte* (tenis/padel/futbol sala)"), deporte);
        addRow(2, new Label("Descripcion*"), descripcion);
        addRow(3, new Label("Operativa"), disponible);
        add(crear, 1, 4);

        crear.setOnAction(e -> {
            try {
                String idTxt = t(id);
                String depTxt = t(deporte);
                String desTxt = t(descripcion);

                boolean ok = club.altaPista(new Pista(idTxt, depTxt, desTxt, disponible.isSelected()));
                if (!ok) {
                    showError("No se pudo dar de alta la pista");
                    return;
                }

                showInfo("Se ha creado correctamente. Se guardara al cerrar o con Archivo -> Guardar.");
                id.clear();
                deporte.clear();
                descripcion.clear();
                disponible.setSelected(false);

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    private String t(TextField tf) { return tf.getText() == null ? "" : tf.getText().trim(); }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
