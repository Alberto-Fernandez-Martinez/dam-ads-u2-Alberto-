package vista.views;

import servicio.ClubDeportivo;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import modelo.Socio;
import modelo.Pista;
import modelo.Reserva;

public class DashboardView extends BorderPane {

    public DashboardView(ClubDeportivo club) {
        setPadding(new Insets(6));

        TableView<Socio> tablaSocios = new TableView<>();
        tablaSocios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Socio, String> s1 = new TableColumn<>("idSocio");
        s1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getIdSocio()));

        TableColumn<Socio, String> s2 = new TableColumn<>("dni");
        s2.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getDni()));

        TableColumn<Socio, String> s3 = new TableColumn<>("nombre");
        s3.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNombre()));

        TableColumn<Socio, String> s4 = new TableColumn<>("apellidos");
        s4.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getApellidos()));

        TableColumn<Socio, String> s5 = new TableColumn<>("telefono");
        s5.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getTelefono()));

        TableColumn<Socio, String> s6 = new TableColumn<>("email");
        s6.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getEmail()));

        tablaSocios.getColumns().addAll(s1, s2, s3, s4, s5, s6);
        tablaSocios.getItems().setAll(club.getSocios());

        BorderPane panelSocios = new BorderPane();
        panelSocios.setTop(new Label("Socios"));
        panelSocios.setCenter(tablaSocios);

        TableView<Pista> tablaPistas = new TableView<>();
        tablaPistas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pista, String> p1 = new TableColumn<>("idPista");
        p1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getIdPista()));

        TableColumn<Pista, String> p2 = new TableColumn<>("deporte");
        p2.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getDeporte()));

        TableColumn<Pista, String> p3 = new TableColumn<>("descripcion");
        p3.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getDescripcion()));

        TableColumn<Pista, String> p4 = new TableColumn<>("disponible");
        p4.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().isDisponible())));

        tablaPistas.getColumns().addAll(p1, p2, p3, p4);
        tablaPistas.getItems().setAll(club.getPistas());

        BorderPane panelPistas = new BorderPane();
        panelPistas.setTop(new Label("Pistas"));
        panelPistas.setCenter(tablaPistas);

        TableView<Reserva> tablaReservas = new TableView<>();
        tablaReservas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Reserva, String> r1 = new TableColumn<>("idReserva");
        r1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getIdReserva()));

        TableColumn<Reserva, String> r2 = new TableColumn<>("idSocio");
        r2.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getIdSocio()));

        TableColumn<Reserva, String> r3 = new TableColumn<>("idPista");
        r3.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getIdPista()));

        TableColumn<Reserva, String> r4 = new TableColumn<>("fecha");
        r4.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFecha() == null ? "" : p.getValue().getFecha().toString()));

        TableColumn<Reserva, String> r5 = new TableColumn<>("horaInicio");
        r5.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getHoraInicio() == null ? "" : p.getValue().getHoraInicio().toString()));

        TableColumn<Reserva, String> r6 = new TableColumn<>("duracionMin");
        r6.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getDuracionMin())));

        TableColumn<Reserva, String> r7 = new TableColumn<>("precio");
        r7.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getPrecio())));

        tablaReservas.getColumns().addAll(r1, r2, r3, r4, r5, r6, r7);
        tablaReservas.getItems().setAll(club.getReservas());

        BorderPane panelReservas = new BorderPane();
        panelReservas.setTop(new Label("Reservas"));
        panelReservas.setCenter(tablaReservas);

        SplitPane topSplit = new SplitPane(panelSocios, panelPistas);
        topSplit.setDividerPositions(0.65);

        VBox layout = new VBox(6, topSplit, panelReservas);
        VBox.setVgrow(topSplit, Priority.ALWAYS);
        VBox.setVgrow(panelReservas, Priority.ALWAYS);

        setTop(null);
        setCenter(layout);
        setRight(null);
        setBottom(null);
    }
}
