package ba.unsa.etf.rpr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        GeografijaDAO.getInstance();
        //System.out.println("Gradovi su:\n" + ispisiGradove());
        //glavniGrad();
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

    }

}
