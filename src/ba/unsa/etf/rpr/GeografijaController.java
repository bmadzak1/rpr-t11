package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class GeografijaController {
    public TextField pretragaDrzavaIme;
    public TextField pretragaGradRezultat;
    public TextField dodajNazivDrzave;
    public TextField brisiNaziv;
    public TextField dodajNazivGrada;
    public Spinner<Integer> dodajBrStan;

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
            GradoviReport.getInstance().showReport(GeografijaDAO.getInstance().getConnection());
        }catch (Exception e){

        }
    }
}
