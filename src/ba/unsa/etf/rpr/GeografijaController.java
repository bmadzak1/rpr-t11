package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GeografijaController {
    public TextField pretragaDrzavaIme;
    public TextField pretragaGradRezultat;
    public TextField dodajNazivDrzave;
    public TextField brisiNaziv;
    public TextField dodajNazivGrada;
    public Spinner<Integer> dodajBrStan;
    public TextField inputReport;

    GeografijaDAO geografijaDAO;

    @FXML
    void initialize(){
        geografijaDAO = GeografijaDAO.getInstance();
    }

    public void pretraga(ActionEvent actionEvent) {
        pretragaGradRezultat.textProperty().setValue("");

        if(pretragaDrzavaIme.textProperty().get() == null)
            return;

        if(geografijaDAO.nadjiDrzavu(pretragaDrzavaIme.textProperty().get().trim()) == null)
            return;

        String naziv = pretragaDrzavaIme.textProperty().get();
        Grad grad = geografijaDAO.glavniGrad(naziv);
        pretragaGradRezultat.textProperty().setValue(grad.getNaziv());
    }

    public void dodaj(ActionEvent actionEvent) {
        if(dodajNazivDrzave.textProperty().get() == null)
            return;
        if(dodajNazivGrada.textProperty().get() == null)
            return;
        if(dodajBrStan.getValue() < 0)
            return;

        if(geografijaDAO.nadjiDrzavu(dodajNazivDrzave.textProperty().get()) != null)
            return;

        Grad grad = new Grad(0, dodajNazivGrada.textProperty().get(), dodajBrStan.getValue());
        Drzava drzava = new Drzava(0, dodajNazivDrzave.textProperty().get(), grad);
        grad.setDrzava(drzava);

        geografijaDAO.dodajDrzavu(drzava);
    }

    public void obrisi(ActionEvent actionEvent) {
        if(brisiNaziv.textProperty().get() == null)
            return;

        if(geografijaDAO.nadjiDrzavu(brisiNaziv.textProperty().get()) == null)
            return;

        geografijaDAO.obrisiDrzavu(brisiNaziv.textProperty().get());
    }

    public void report(ActionEvent actionEvent) {
        try {
            GradoviReport report = new GradoviReport();
            report.showReport(GeografijaDAO.getInstance().getConnection());
        }catch (Exception e){

        }
    }

    public void bosLan(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("bs", "BA"));
        reload();
    }

    public void enLan(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("en", "US"));
        reload();
    }

    public void deLan(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("de", "DE"));
        reload();
    }

    public void frLan(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("fr", "FR"));
        reload();
    }

    void reload(){
        try {
            Stage primaryStage = (Stage) pretragaDrzavaIme.getScene().getWindow();
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            Parent root = FXMLLoader.load(getClass().getResource("Geografija.fxml"), bundle);
            primaryStage.setTitle("Geografija");
            primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            primaryStage.setResizable(false);
            primaryStage.show();
        }catch (Exception e){

        }
    }

    public void saveReport(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF", "*.pdf");
        FileChooser.ExtensionFilter docx = new FileChooser.ExtensionFilter("DOCX", "*.docx");
        FileChooser.ExtensionFilter xslx = new FileChooser.ExtensionFilter("XSLX", "*.xslx");
        chooser.getExtensionFilters().add(pdf);
        chooser.getExtensionFilters().add(docx);
        chooser.getExtensionFilters().add(xslx);
        File f = chooser.showSaveDialog(null);

        if(f != null){
            try{
                GradoviReport report = new GradoviReport();
                System.out.println(f.getAbsolutePath());
                report.saveAs(f.getAbsolutePath());
            }catch (Exception e){

            }
        }
    }

    public void reportBtn2Press(ActionEvent actionEvent) {
        Integer id = 0;

        if(inputReport.textProperty().get() != null) {
            Drzava drzava = GeografijaDAO.getInstance().nadjiDrzavu(inputReport.textProperty().get());
            if(drzava != null)
                id = drzava.getId();
        }

        try {
            GradoviReport report = new GradoviReport();
            report.showReportCountry(GeografijaDAO.getInstance().getConnection(), id);
        }catch (Exception e){

        }
    }
}
