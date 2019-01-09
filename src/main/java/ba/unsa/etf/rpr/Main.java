package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import static javafx.application.Application.launch;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        Parent root = FXMLLoader.load(getClass().getResource("/Geografija.fxml"),bundle);
        primaryStage.setTitle("Geografija");
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static String ispisiGradove(){
        String gradovi = new String();
        for(Grad g : GeografijaDAO.getInstance().getGradovi()) {
            System.out.println(g.toString());
            gradovi += g.toString();
        }
        return gradovi;
    }

    public static void glavniGrad(){
        Scanner ulaz = new Scanner(System.in);
        GeografijaDAO geografijaDAO = GeografijaDAO.getInstance();
        System.out.println("Unesi drzavu: ");
        String drzava = ulaz.nextLine().trim();
        Grad grad = geografijaDAO.glavniGrad(drzava);
        if(grad != null)
            System.out.println("Glavni grad " + drzava + " je " + grad.getNaziv());
    }

}
