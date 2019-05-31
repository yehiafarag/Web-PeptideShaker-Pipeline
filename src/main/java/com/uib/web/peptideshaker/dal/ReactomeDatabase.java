package com.uib.web.peptideshaker.dal;

import com.uib.web.peptideshaker.presenter.core.graph.Edge;
import com.vaadin.server.VaadinSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents database layer that interact with mySQL database that
 * have Reactome data
 *
 * @author Yehia Farag
 */
public class ReactomeDatabase {

    /**
     * Database URL.
     */
    private String dbURL;
    /**
     * Database name.
     */
    private String dbName;
    /**
     * Database driver.
     */
    private String dbDriver;
    /**
     * Database username.
     */
    private String dbUserName;
    /**
     * Database password.
     */
    private String dbPassword;
    private Connection conn = null;

    public ReactomeDatabase() {
        this.dbURL = VaadinSession.getCurrent().getAttribute("dbURL").toString();
        this.dbDriver = VaadinSession.getCurrent().getAttribute("dbDriver").toString();
        this.dbUserName = VaadinSession.getCurrent().getAttribute("dbUserName").toString();
        this.dbPassword = VaadinSession.getCurrent().getAttribute("dbPassword").toString();
        this.dbName = this.getDBName();

    }

    private String getDBName() {
        String databaseName = null;
        try {

            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + "mysql", dbUserName, dbPassword);

            }
            //temp 
            Statement statement = conn.createStatement();
            String sqoDataBase = "SHOW DATABASES;";
            ResultSet rs1 = statement.executeQuery(sqoDataBase);
            while (rs1.next()) {
                String db = rs1.getString("Database");
                if (db.contains("reactome_")) {
                    databaseName = db;
                }

            }
            conn.close();
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return databaseName;
    }

    public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
        try {

            Set<String[]> edges = new LinkedHashSet<>();
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
            }
            String selectstatment = "SELECT `indexes` FROM `accessionindex`  where ";
            for (String str : proteinAcc) {
                if (str.trim().equalsIgnoreCase("")) {
                    continue;
                }
                selectstatment = selectstatment + "`uniprot_acc`=? or ";

            }
            if (selectstatment.endsWith("where ")) {
                return edges;
            }
            selectstatment = selectstatment.substring(0, selectstatment.length() - 4) + ";";
            PreparedStatement selectNameStat = conn.prepareStatement(selectstatment);
            int indexer = 1;
            for (String str : proteinAcc) {
                if (str.trim().equalsIgnoreCase("")) {
                    continue;
                }
                selectNameStat.setString(indexer++, str);
            }
            ResultSet rs = selectNameStat.executeQuery();
            Set<String> lines = new HashSet<>();
            while (rs.next()) {
                lines.add(rs.getString("indexes"));
            }
            selectNameStat.close();
            rs.close();
            Set<Integer> indexes = new HashSet<>();

            lines.stream().map((line) -> line.substring(1, line.length() - 1).split(",")).forEachOrdered((strIndexes) -> {

                for (String strIndex : strIndexes) {
                    try {
                        if (strIndex.trim().equals("")) {
                            continue;
                        }
                        indexes.add(Integer.valueOf(strIndex.trim()));
                    } catch (NumberFormatException exp) {
                        System.out.println("at error in number-" + strIndex + "-");
                        exp.printStackTrace();
                    }
                }
            });

            selectstatment = "SELECT * FROM `edges`  where ";
            for (Integer i : indexes) {
                selectstatment = selectstatment + "`reaction_index`=? or ";

            }
            selectstatment = selectstatment.substring(0, selectstatment.length() - 4) + ";";
            selectNameStat = conn.prepareStatement(selectstatment);
            indexer = 1;
            for (Integer i : indexes) {
                selectNameStat.setInt(indexer++, i);
            }

            rs = selectNameStat.executeQuery();

            while (rs.next()) {
                edges.add(new String[]{rs.getString(2).trim(), rs.getString(3).trim(), rs.getString(4).trim(), rs.getString(5).trim(), rs.getString(6).trim(), rs.getString(7).trim()});
            }
            return edges;

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }
    public void getFullPathways(){
    
    
    }

}
