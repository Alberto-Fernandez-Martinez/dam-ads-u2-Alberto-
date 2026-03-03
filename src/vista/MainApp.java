package vista;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import servicio.ClubDeportivo;
import vista.views.BajaSocioView;
import vista.views.CambiarDisponibilidadView;
import vista.views.CancelarReservaView;
import vista.views.DashboardView;
import vista.views.PistaFormView;
import vista.views.ReservaFormView;
import vista.views.SocioFormView;

import java.sql.SQLException;

/**
 * Aplicacion JavaFX principal del gestor del club deportivo.
 */
public class MainApp extends Application {

    private ClubDeportivo club;
    private BorderPane root;
    private Label status;

    /**
     * Inicializa la aplicacion, crea el servicio y monta la UI principal.
     *
     * @param stage ventana principal de JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            club = new ClubDeportivo();
            showInfo("Conexion con base de datos establecida correctamente.");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            Platform.exit();
            return;
        }

        root = new BorderPane();
        root.setTop(buildMenuBar());
        status = new Label("Listo");
        status.setPadding(new Insets(4));
        root.setBottom(status);

        root.setCenter(new DashboardView(club));

        Scene scene = new Scene(root, 960, 640);
        stage.setTitle("Club DAMA Sports");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            try {
                if (club != null) {
                    club.cerrar();
                }
            } catch (Exception ex) {
                event.consume();
                showError("Error guardando/cerrando: " + ex.getMessage());
            }
        });
        stage.show();
    }

    private MenuBar buildMenuBar() {
        MenuBar mb = new MenuBar();

        Menu socios = new Menu("Socios");
        MenuItem altaSocio = new MenuItem("Alta socio");
        altaSocio.setOnAction(e -> root.setCenter(new SocioFormView(club)));
        MenuItem bajaSocio = new MenuItem("Baja socio");
        bajaSocio.setOnAction(e -> root.setCenter(new BajaSocioView(club)));
        socios.getItems().addAll(altaSocio, bajaSocio);

        Menu pistas = new Menu("Pistas");
        MenuItem altaPista = new MenuItem("Alta pista");
        altaPista.setOnAction(e -> root.setCenter(new PistaFormView(club)));
        MenuItem cambiarDisp = new MenuItem("Cambiar disponibilidad");
        cambiarDisp.setOnAction(e -> root.setCenter(new CambiarDisponibilidadView(club)));
        pistas.getItems().addAll(altaPista, cambiarDisp);

        Menu reservas = new Menu("Reservas");
        MenuItem crearReserva = new MenuItem("Crear reserva");
        crearReserva.setOnAction(e -> root.setCenter(new ReservaFormView(club)));
        MenuItem cancelarReserva = new MenuItem("Cancelar reserva");
        cancelarReserva.setOnAction(e -> root.setCenter(new CancelarReservaView(club)));
        reservas.getItems().addAll(crearReserva, cancelarReserva);

        Menu ver = new Menu("Ver");
        MenuItem dashboard = new MenuItem("Dashboard");
        dashboard.setOnAction(e -> root.setCenter(new DashboardView(club)));
        ver.getItems().add(dashboard);

        Menu archivo = new Menu("Archivo");
        MenuItem guardar = new MenuItem("Guardar");
        guardar.setOnAction(e -> {
            try {
                if (!club.hayCambiosPendientes()) {
                    showInfo("No hay cambios pendientes para guardar.");
                    return;
                }
                club.guardarDatos();
                showInfo("Cambios guardados correctamente en la base de datos.");
            } catch (Exception ex) {
                showError("Error guardando: " + ex.getMessage());
            }
        });

        MenuItem salir = new MenuItem("Salir");
        salir.setOnAction(e -> {
            try {
                if (club != null) {
                    club.cerrar();
                }
                Platform.exit();
            } catch (Exception ex) {
                showError("Error guardando/cerrando: " + ex.getMessage());
            }
        });

        archivo.getItems().addAll(guardar, new SeparatorMenuItem(), salir);
        mb.getMenus().addAll(archivo, socios, pistas, reservas, ver);
        return mb;
    }

    /**
     * Muestra un dialogo informativo.
     *
     * @param msg mensaje a mostrar.
     */
    public void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    /**
     * Muestra un dialogo de error.
     *
     * @param msg mensaje de error.
     */
    public void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    /**
     * Cierra recursos al detener la aplicacion.
     *
     * @throws Exception si falla el cierre del servicio.
     */
    @Override
    public void stop() throws Exception {
        if (club != null) {
            club.cerrar();
        }
        super.stop();
    }

    /**
     * Punto de entrada de la aplicacion JavaFX.
     *
     * @param args argumentos de linea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
