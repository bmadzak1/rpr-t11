package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {

    private static GeografijaDAO instance = null;
    private Connection conn;

    private ArrayList<Grad> gradovi;

    private static int gradID = 6;
    private static int drzavaID = 1;

    private PreparedStatement selectGrad;
    private PreparedStatement selectDrzava;
    private PreparedStatement addGrad;
    private PreparedStatement addDrzava;
    private PreparedStatement deleteGrad;
    private PreparedStatement deleteDrzava;
    private PreparedStatement findGrad;
    private PreparedStatement findGlavniGrad;
    private PreparedStatement findDrzava;
    private PreparedStatement deleteDrzavaByName;
    private PreparedStatement deleteGradFromDrzava;
    private PreparedStatement getGradoveSort;
    private PreparedStatement getDrzavaGrada;
    private PreparedStatement findDrzavaByGlavniGrad;
    private PreparedStatement changeGrad;
    private PreparedStatement updateDrzavaGrada;
    private PreparedStatement updateGradDrzave;

    private static void initialize(){
        instance = new GeografijaDAO();
    }

    private GeografijaDAO(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::resource:baza.db");

            kreirajBazu();

            //STATEMENTS
            selectGrad = conn.prepareStatement("SELECT * FROM grad");
            selectDrzava = conn.prepareStatement("SELECT * FROM drzava");
            addGrad = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            addDrzava = conn.prepareStatement("INSERT INTO drzava VALUES(?, ?, ?)");
            deleteGrad = conn.prepareStatement("DELETE FROM grad WHERE id IS NOT NULL");
            deleteDrzava = conn.prepareStatement("DELETE FROM drzava WHERE id IS NOT NULL");
            findGlavniGrad = conn.prepareStatement("SELECT * FROM grad WHERE id = (SELECT glavni_grad FROM drzava WHERE naziv = ?)");
            findDrzava = conn.prepareStatement("SELECT * FROM drzava WHERE naziv = ?");
            deleteDrzavaByName = conn.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            deleteGradFromDrzava = conn.prepareStatement("DELETE FROM grad WHERE drzava = (SELECT id FROM drzava WHERE naziv = ?)");
            getGradoveSort = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            getDrzavaGrada = conn.prepareStatement("SELECT naziv FROM drzava WHERE id = ?");
            findDrzavaByGlavniGrad = conn.prepareStatement("SELECT * FROM drzava WHERE glavni_grad = ?");
            changeGrad = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ? WHERE id = ?");
            updateDrzavaGrada = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
            updateGradDrzave = conn.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE id = ?");
            findGrad = conn.prepareStatement("SELECT * FROM grad WHERE naziv = ?");
            //END STATEMENTS

            ResultSet gradSet = selectGrad.executeQuery();
            ResultSet drzavaSet = selectDrzava.executeQuery();

            if(!gradSet.next() || !drzavaSet.next())
                napuni();

        }catch (SQLException e) {
            System.out.println("Neuspješna konekcija na bazu");
        }
    }

    public Connection getConnection(){
        return conn;
    }

    public static GeografijaDAO getInstance(){
        if(instance == null)
            initialize();
        return instance;
    }

    public static void removeInstance(){
        instance = null;
    }

    private void kreirajBazu(){
        boolean kreiraj = false;
        try{
            conn.createStatement().executeQuery("SELECT * FROM grad");
            conn.createStatement().executeQuery("SELECT * FROM drzava");
        }catch (Exception e) {
            kreiraj = true;
            try {
                conn.createStatement().executeUpdate("DROP TABLE grad");
            } catch (Exception e1) {
            }
            try {
                conn.createStatement().executeUpdate("DROP TABLE drzava");
            } catch (Exception e2) {
            }
        }

        String createGrad = "CREATE TABLE grad (\n" +
                "  id integer not null primary key ,\n" +
                "  naziv text not null ,\n" +
                "  broj_stanovnika integer\n" +
                ");\n";
        String createDrzava = "CREATE TABLE drzava (\n" +
                "  id integer not null primary key ,\n" +
                "  naziv text not null ,\n" +
                "  glavni_grad integer references grad(id)\n" +
                ");\n";
        String updateGrad = "ALTER TABLE grad ADD COLUMN drzava integer references drzava(id);";

        if(!kreiraj)
            return;
        try{
            conn.createStatement().executeUpdate(createGrad);
            conn.createStatement().executeUpdate(createDrzava);
            conn.createStatement().executeUpdate(updateGrad);
        }catch (Exception e2){
            System.out.println("Greska");
        }
    }

    private void napuni(){
        try {
            deleteGrad.executeUpdate();
            deleteDrzava.executeUpdate();
        }catch (Exception e){

        }

        Grad london = new Grad(1, "London", 8825000);
        Grad pariz = new Grad(2, "Pariz", 2206488);
        Grad bec = new Grad(3, "Beč", 1899055);
        Grad manchester = new Grad(4, "Manchester", 545500);
        Grad graz = new Grad(5, "Graz", 280200);
        Drzava francuska = new Drzava(101, "Francuska",  pariz);
        Drzava gb = new Drzava(103, "Velika Britanija", london);
        Drzava austrija = new Drzava(105, "Austrija", bec);
        london.setDrzava(gb);
        pariz.setDrzava(francuska);
        bec.setDrzava(austrija);
        manchester.setDrzava(gb);
        graz.setDrzava(austrija);

        dodajGrad(london);
        dodajGrad(pariz);
        dodajGrad(bec);
        dodajGrad(manchester);
        dodajGrad(graz);
    }

    public Grad glavniGrad(String drzava){
        try {
            findDrzava.setString(1, drzava);
            ResultSet drzavaSet = findDrzava.executeQuery();
            if(!drzavaSet.next()) {
                return null;
            }

            findGlavniGrad.setString(1, drzava);
            ResultSet gradSet = findGlavniGrad.executeQuery();
            if(gradSet.next()) {
                Grad grad = new Grad(gradSet.getInt(1), gradSet.getString(2), gradSet.getInt(3));
                Drzava drzava1 = new Drzava(drzavaSet.getInt(1), drzavaSet.getString(2), grad);
                grad.setDrzava(drzava1);
                return grad;
            }
            return null;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void obrisiDrzavu(String drzava){
        try {
            findDrzava.setString(1, drzava);
            ResultSet set = findDrzava.executeQuery();
            if(!set.next())
                return;

            deleteDrzavaByName.setString(1, drzava);
            deleteGradFromDrzava.setString(1, drzava);
            deleteGradFromDrzava.executeUpdate();
            deleteDrzava.executeUpdate();
        }catch (Exception e ){

        }
    }

    public ArrayList<Grad> gradovi(){
        ArrayList<Grad> gradovi = new ArrayList<Grad>();
        try{
            ResultSet gradoviSet = getGradoveSort.executeQuery();
            while (gradoviSet.next()){
                Grad grad = new Grad(gradoviSet.getInt(1), gradoviSet.getString(2), gradoviSet.getInt(3));
                getDrzavaGrada.setInt(1, gradoviSet.getInt(4));
                ResultSet drzava = getDrzavaGrada.executeQuery();
                if(drzava.next()){
                    Drzava d = nadjiDrzavu(drzava.getString(1));
                    grad.setDrzava(d);
                }
                gradovi.add(grad);
            }
        }catch (Exception e){

        }
        return gradovi;
    }


    public void dodajGrad(Grad grad){
        try {
            for(Grad g : gradovi())
                if(g.getNaziv().equalsIgnoreCase(grad.getNaziv()))
                    return;

            Drzava d = nadjiDrzavu(grad.getDrzava().getNaziv());
            int drzavaIDTemp;
            if(d != null)
                drzavaIDTemp = d.getId();
            else
                drzavaIDTemp = drzavaID++;

            int id;
            if(grad.getId() == 0)
                id = gradID++;
            else
                id = grad.getId();

            addGrad.setInt(1, id);
            addGrad.setString(2, grad.getNaziv());
            addGrad.setInt(3, grad.getBrojStanovnika());
            addGrad.executeUpdate();

            if(d == null){
                addDrzava.setInt(1, drzavaIDTemp);
                addDrzava.setString(2, grad.getDrzava().getNaziv());
                addDrzava.setInt(3, id);
                addDrzava.executeUpdate();
            }

            updateDrzavaGrada.setInt(1, drzavaIDTemp);
            updateDrzavaGrada.setInt(2, id);
            updateDrzavaGrada.executeUpdate();
        }catch (Exception e){
            System.out.println("greska dodaj");
        }
    }

    public void dodajDrzavu(Drzava drzava){
        try{
            Drzava d = nadjiDrzavu(drzava.getNaziv());
            if(d != null)
                return;

            int id;

            if(drzava.getId() == 0) {
                id = drzavaID++;
                drzava.setId(id);
            }
            else
                id = drzava.getId();

            addDrzava.setInt(1, id);
            addDrzava.setString(2, drzava.getNaziv());
            addDrzava.executeUpdate();

            dodajGrad(drzava.getGlavniGrad());

            findGrad.setString(1,drzava.getGlavniGrad().getNaziv());
            ResultSet gradSet = findGrad.executeQuery();

            int gradID = gradSet.getInt(1);

            updateGradDrzave.setInt(1, gradID);
            updateGradDrzave.setInt(2, id);
            updateGradDrzave.executeUpdate();
        }catch (Exception e){

        }
    }

    public void izmijeniGrad(Grad grad){
        try{
            changeGrad.setString(1, grad.getNaziv());
            changeGrad.setInt(2, grad.getBrojStanovnika());
            changeGrad.setInt(3, grad.getId());
            changeGrad.executeUpdate();
        }catch (Exception e){

        }
    }

    Drzava nadjiDrzavu(String drzava){
        Drzava drz = null;
        try{
            findDrzava.setString(1, drzava);
            ResultSet drzavaSet = findDrzava.executeQuery();
            if(drzavaSet.next()){
                drz = new Drzava(drzavaSet.getInt(1), drzavaSet.getString(2), null);
                findGlavniGrad.setString(1,drzavaSet.getString(2));
                ResultSet gradSet = findGlavniGrad.executeQuery();
                if(gradSet.next()){
                    Grad g  = new Grad(gradSet.getInt(1), gradSet.getString(2), gradSet.getInt(3));
                    g.setDrzava(drz);
                    drz.setGlavniGrad(g);
                }
                return drz;
            }else{
                return null;
            }
        }catch (Exception e){
            System.out.println("nadjiDrzavu e");
            return null;
        }
    }

    public ArrayList<Grad> getGradovi() {
        return gradovi;
    }

    public void setGradovi(ArrayList<Grad> gradovi) {
        this.gradovi = gradovi;
    }
}
