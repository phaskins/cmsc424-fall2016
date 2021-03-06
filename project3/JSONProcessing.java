import java.sql.*;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


public class JSONProcessing 
{
	public static void processJSON(String json) {
		/************* 
		 * Add your code to insert appropriate tuples into the database.
		 ************/
		
		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");
       	 	try {
          		Class.forName("org.postgresql.Driver");
       		 } catch (ClassNotFoundException e) {
           		 System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
          		  e.printStackTrace();
           		 return;
       		 }

       		 System.out.println("PostgreSQL JDBC Driver Registered!");
       		 Connection connection = null;
	      	 try {
            		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/flightsskewed","vagrant", "vagrant");
      		 } catch (SQLException e) {
           		System.out.println("Connection Failed! Check output console");
            		e.printStackTrace();
            		return;
        	 }

        	if (connection != null) {
         	   System.out.println("You made it, take control your database now!");
       		 } else {
         	   System.out.println("Failed to make connection!");
            	   return;
    		 }
		
		
	    try {
			
	    JSONParser parser = new JSONParser();
	    Object obj = parser.parse(json);
		    
	    JSONObject jsonObject = (JSONObject) obj;
	    Statement stmt = null;
			
			
		
		if (json.contains("newcustomer") == true) {
			
			JSONObject newcustomer = (JSONObject) jsonObject.get("newcustomer");
			String customerid = (String) newcustomer.get("customerid");
			String name = (String) newcustomer.get("name");
			String birthdate = (String) newcustomer.get("birthdate");
			String frequentflieron = (String) newcustomer.get("frequentflieron");
			
			try {
				String query = "select * from customers where customerid = " + "'" + customerid + "'" + ";";
				System.out.println(query);
				stmt = connection.createStatement();
            			ResultSet rs = stmt.executeQuery(query);
				
				if (rs.next()) {
					System.out.println("This customer already exists!");
            	   			return;		
				}
				
				
				String q = "select * from airlines where name = " + "'" + frequentflieron + "'" + ";";
				System.out.println(q);
				rs = stmt.executeQuery(q);
				
				
				if (!rs.next()) {
					System.out.println("The frequentflieron name does not exists!");
            	   			return;		
				}
						
				else { 
					
					String query1 = "select airlineid from airlines where name = " + "'" + frequentflieron + "'" + ";";
					System.out.println(query1);
					
            				rs = stmt.executeQuery(query1);
					rs.next();
            			
                			String airlineid = rs.getString("airlineid");
				
               				String query2 = "INSERT into customers VALUES(" + "'" + customerid + "'," + "'" + name + "', to_date(" + "'" + birthdate + "'" + ", 'yyyy-mm-dd')," + "'" + airlineid + "'" + ");";
					System.out.println(query2);
					
            				stmt.executeQuery(query2);
				}
						
            		stmt.close();
				
        		} catch (SQLException e) {
          			  System.out.println(e);
        		}
		}
		
		else if (json.contains("flightinfo") == true) {
			
			JSONObject flightinfo = (JSONObject) jsonObject.get("flightinfo");
			String flightid = (String) flightinfo.get("flightid");
			String flightdate = (String) flightinfo.get("flightdate");
			
			JSONArray customers = (JSONArray) flightinfo.get("customers");
			int array_length = customers.size();
			
			for (int i = 0; i < array_length; i++) {
				
				JSONObject customer_info = (JSONObject) customers.get(i);
				String customerid = (String) customer_info.get("customer_id");
				
			   try {
				   			
				String q = "select * from customers where customerid = " + "'" + customerid + "'" + ";";
				System.out.println(q);
				stmt = connection.createStatement();
            			ResultSet rs = stmt.executeQuery(q);
				
				if (rs.next()) {
					
					String query = "INSERT into flewon VALUES(" + "'" + flightid + "'" + "," + "'" + customerid + "', to_date(" + "'" + flightdate + "'" + ", 'yyyy-mm-dd'));";
            				System.out.println(query);
					stmt.executeQuery(query);
				}
				
				else {

					String name = (String) customer_info.get("name");
					String birthdate = (String) customer_info.get("birthdate");
					String frequentflieron = (String) customer_info.get("frequentflieron");
					
					String query1 = "select * from airlines where airlineid = " + "'" + frequentflieron + "'" + ";"; 
					System.out.println(query1);
            				rs = stmt.executeQuery(query1);
				
					if (!rs.next()) {
						System.out.println("The frequentflieron code does not exists!");
            	   				return;		
					}
					
					
               				String query2 = "INSERT into customers VALUES(" + "'" + customerid + "'," + "'" + name + "', to_date(" + "'" + birthdate + "'" + ", 'yyyy-mm-dd')," + "'" + frequentflieron + "'" + ");";
					System.out.println(query2);
            				stmt.executeQuery(query2);
					
					
					String query3 = "INSERT into flewon VALUES(" + "'" + flightid + "'" + "," + "'" + customerid + "', to_date(" + "'" + flightdate + "'" + ", 'yyyy-mm-dd'));";
            				System.out.println(query3);
					stmt.executeQuery(query3);					
					
				}
			
			    stmt.close();
					
			    } catch (SQLException e) {
          			  System.out.println(e);
        		    }
					
			}
			
			
		}
		
		else {
			System.out.println("The update cannot be supported");
		}
		    
	    } catch (ParseException e) {
		System.out.println("Failure to parse");
	    }
		
		
		System.out.println("Adding data from " + json + " into the database");
	}

		
	
	public static void main(String[] argv) {
		Scanner in_scanner = new Scanner(System.in);

		while(in_scanner.hasNext()) {
			String json = in_scanner.nextLine();
			processJSON(json);
		}
	}
}
