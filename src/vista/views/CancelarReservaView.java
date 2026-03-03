package vista.views;

import servicio.ClubDeportivo;
import modelo.Reserva;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class CancelarReservaView extends GridPane {

    public CancelarReservaView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Reserva> comboReserva = new ComboBox<>();
        Button cancelar = new Button("Cancelar reserva");

        comboReserva.getItems().setAll(club.getReservas());
        comboReserva.setConverter(new StringConverter<>() {
            @Override
            public String toString(Reserva r) {
                if (r == null) return "";
                return r.getIdReserva() + " - " + r.getFecha() + " " + r.getHoraInicio()
                        + " (Socio " + r.getIdSocio() + ", Pista " + r.getIdPista() + ")";
            }

            @Override
            public Reserva fromString(String string) { return null; }
        });

        addRow(0, new Label("Reserva"), comboReserva);
        add(cancelar, 1, 1);

        cancelar.setOnAction(e -> {
            try {
                Reserva sel = comboReserva.getValue();
                String idReserva = sel == null ? null : sel.getIdReserva();
                boolean ok = club.cancelarReserva(idReserva);
                if (!ok) {
                    showError("No se pudo cancelar la reserva");
                    return;
                }

                showInfo("Se ha cancelado correctamente. Se guardara al cerrar o con Archivo -> Guardar.");
                comboReserva.getItems().setAll(club.getReservas());

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
