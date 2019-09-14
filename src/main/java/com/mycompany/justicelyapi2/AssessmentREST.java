/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.justicelyapi2;

/**
 *
 * @author Sonti Rametse
 */
import entities.assessment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Sonti Rametse
 */
@Path("assessment")
public class AssessmentREST {

    public Connection con = null;
    public String user = "b0ce1d0d5e2f51";
    public String pass = "110ea673";
                  
    @GET
    @Path("{id}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getTest() {
        return "testing 12345";
    }
    
    @GET
    @Path("/findAll")
    @Produces({MediaType.APPLICATION_JSON})
    public ArrayList<assessment> findAll() throws ClassNotFoundException, SQLException {
        ArrayList<assessment> assess = new ArrayList<>();
        String query = "SELECT * FROM assessment WHERE active = 1";
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://us-cdbr-iron-east-02.cleardb.net:3306/heroku_43f90d773d07a4c", user, pass);
        Statement sta = con.createStatement();
        ResultSet result;
        result = sta.executeQuery(query);
        
        while(result.next())
        {
            assessment ass = new assessment();
            ass.setId(result.getInt("id"));
            ass.setQuestion(result.getString("question"));
            ass.setWeight(result.getDouble("weight"));
            assess.add(ass);
        }
        con.close();
        
        return assess;
    }
    
    @GET
    @Path("/findAbuseLevel/{resultList}")
    @Produces({MediaType.TEXT_PLAIN})
    public String findAbuseLevel(@PathParam("resultList") String resultList) throws ClassNotFoundException, SQLException {
        String result = "low";
        //Example:01010
        
        if(resultList.isEmpty() == false)
        {
            String resultList2 = "";
            ArrayList<Integer> intResults = new ArrayList<>();
            Integer counter = 1;
            
            for(int i=0;i < resultList.length();i++)
            {   
                char str = resultList.charAt(i);
                
                if("1".equals(Character.toString(resultList.charAt(i))))
                {    
                    if("".equals(resultList2))
                        resultList2 = counter.toString();
                    else
                        resultList2 = resultList2 + "," + counter.toString();
                    
                    intResults.add(counter);
                }
                counter++;
            }
            
            if(intResults.isEmpty() == false)
            {               
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://us-cdbr-iron-east-02.cleardb.net:3306/heroku_43f90d773d07a4c", user, pass);
                Statement sta = con.createStatement();
                
                String query1 = "SELECT sum(weight) as total FROM assessment";  
                ResultSet result1 = sta.executeQuery(query1);
                Double total = 0.0;
                if(result1.next())
                {
                    total = result1.getDouble("total");
                    result1.close();
                }                
                             
                String query2 = "SELECT sum(weight) as total FROM assessment WHERE id IN (" + resultList2 + ")";
                ResultSet result2 = sta.executeQuery(query2);
                Double answerTotal = 0.0;
                if(result2.next())
                {
                    answerTotal = result2.getDouble("total");
                    result2.close();
                }

                Double score =  (answerTotal / total) * 100;

                if (score >= 60) {result = "1";}
                if (score >= 30 && score < 60) {result = "2";}
                if (score < 30){ result = "3";}           
            }            
        }        
        return result;
    }
    
}
