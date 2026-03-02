package vista.views;

import servicio.ClubDeportivo;
import modelo.Pista;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class CambiarDisponibilidadView extends GridPane {

    public CambiarDisponibilidadView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Pista> comboPista = new ComboBox<>();
        CheckBox disponible = new CheckBox("Disponible");
        Button cambiar = new Button("Aplicar");

        comboPista.getItems().setAll(club.getPistas());
        comboPista.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pista p) {
                if (p == null) return "";
                return p.getIdPista() + " - " + p.getDeporte() + " (disp: " + p.isDisponible() + ")";
            }

            @Override
            public Pista fromString(String string) { return null; }
        });

        comboPista.setOnAction(e -> {
            Pista sel = comboPista.getValue();
            if (sel != null) disponible.setSelected(sel.isDisponible());
        });

        addRow(0, new Label("Pista"), comboPista);
        addRow(1, new Label("Estado"), disponible);
        add(cambiar, 1, 2);

        cambiar.setOnAction(e -> {
            try {
                Pista sel = comboPista.getValue();
                String idPista = sel == null ? null : sel.getIdPista();
                boolean ok = club.cambiarDisponibilidadPista(idPista, disponible.isSelected());
                if (!ok) {
                    showError("No se pudo cambiar la disponibilidad");
                    return;
                }

                showInfo("Se ha cambiado correctamente. Se guardara al cerrar o con Archivo -> Guardar.");
                comboPista.getItems().setAll(club.getPistas());

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

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
