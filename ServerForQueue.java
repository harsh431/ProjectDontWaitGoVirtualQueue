/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverforqueue;
import java.net.*;
import java.sql.*;
import java.io.*;

/**
 *
 * @author harsh
 */
public class ServerForQueue {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        // TODO code application logic here
        while(true)
        {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/authentication","root","");
        
        ServerSocket ss= new ServerSocket(5982);
        System.out.println(ss.getInetAddress());
        Socket s = ss.accept();
        System.out.println("A connection was established with the client.");
        
        InputStream is= s.getInputStream();
        OutputStream os = s.getOutputStream();
        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);
        String type = dis.readUTF();
        if(type.equals("validate"))
        {
            System.out.println("Validating data");
                String user = dis.readUTF();
                String pass = dis.readUTF();
                PreparedStatement ps = con.prepareStatement("select * from userinfo");
               ResultSet rs = ps.executeQuery();
               boolean flag = false;
               while(rs.next())
               {
                   if(user.equals(rs.getString(1)) && pass.equals(rs.getString(2)))
                   {
                        dos.writeUTF("valid");
                        flag = true;
                   }

               }
               if(!flag)
               {
                   dos.writeUTF("invalid");
               }
        }
        if(type.equals("store"))
        {
            System.out.println("Storing data....");
            PreparedStatement ps = con.prepareStatement("insert into userinfo values(?,?,?,?)");
            ps.setString(1,dis.readUTF());
            ps.setString(2,dis.readUTF());
            ps.setString(3,dis.readUTF());
            ps.setString(4,dis.readUTF());
            System.out.println("Sign up entry successful. Rows affected : "+ps.executeUpdate());
        }
        dos.close();
        dis.close();
        is.close();
        os.close();
        s.close();
        ss.close();
        }
        
        
    }
         
    }
    

