package vista.views;

import modelo.*;
import servicio.ClubDeportivo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaFormView extends GridPane {

    public ReservaFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        TextField id = new TextField();
        ComboBox<Socio> comboSocio = new ComboBox<>();
        ComboBox<Pista> comboPista = new ComboBox<>();
        DatePicker fecha = new DatePicker(LocalDate.now());
        TextField hora = new TextField("10:00");
        Spinner<Integer> duracion = new Spinner<>(30, 300, 60, 30);
        TextField precio = new TextField("Automatico (BD)");
        precio.setEditable(false);
        Button crear = new Button("Reservar");

        comboSocio.getItems().setAll(club.getSocios());
        comboPista.getItems().setAll(club.getPistas());

        comboSocio.setConverter(new StringConverter<>() {
            @Override
            public String toString(Socio s) {
                if (s == null) return "";
                return s.getIdSocio() + " - " + s.getNombre();
            }

            @Override
            public Socio fromString(String string) { return null; }
        });

        comboPista.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pista p) {
                if (p == null) return "";
                return p.getIdPista() + " - " + p.getDeporte() + " (disp: " + p.isDisponible() + ")";
            }

            @Override
            public Pista fromString(String string) { return null; }
        });

        addRow(0, new Label("idReserva*"), id);
        addRow(1, new Label("Socio*"), comboSocio);
        addRow(2, new Label("Pista*"), comboPista);
        addRow(3, new Label("Fecha*"), fecha);
        addRow(4, new Label("Hora inicio* (HH:mm)"), hora);
        addRow(5, new Label("Duracion (min)"), duracion);
        addRow(6, new Label("Precio (EUR)"), precio);
        add(crear, 1, 7);

        crear.setOnAction(e -> {
            try {
                String idTxt = t(id);
                Socio s = comboSocio.getValue();
                Pista p = comboPista.getValue();
                LocalDate f = fecha.getValue();

                LocalTime t;
                try {
                    t = LocalTime.parse(hora.getText().trim());
                } catch (Exception parse) {
                    showError("Hora invalida. Usa HH:mm (por ejemplo 10:00)");
                    return;
                }

                String idSocio = s == null ? null : s.getIdSocio();
                String idPista = p == null ? null : p.getIdPista();
                Reserva r = new Reserva(idTxt, idSocio, idPista, f, t, duracion.getValue(), 0);
                boolean ok = club.crearReserva(r);
                if (!ok) {
                    showError("No se pudo crear la reserva");
                    return;
                }

                showInfo("Se ha creado correctamente. Se guardara al cerrar o con Archivo -> Guardar. El precio final se calcula en BD.");
                id.clear();

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
