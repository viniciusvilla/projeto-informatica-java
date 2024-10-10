/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projeto_sc.db;
import java.sql.*;

/**
 *
 * @author ninguem
 */
public class ModuloConexao {
    public static Connection conector(){
        java.sql.Connection conexao = null;
        
        //a linha abaixo chama o Driver
        String driver = "com.mysql.cj.jdbc.Driver";
        
        //Armazenando informações referente ao banco
        String url = "jdbc:mysql://localhost:3306/dbinfox?characterEnconding=utf-8";
        String user= "dba";
        String password = "Infox@123456";
        
        //Estabelecendo conexão
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            //Mostra o erro caso não consiga conectar
            e.printStackTrace();
            return null;
        }
    }
}
