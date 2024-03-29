package br.com.devdojo.javaCliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;

public class JavaCliente {
    public static void main (String[] args){
        HttpURLConnection connection = null;

        String user = "isa";
        String password = "rockblin0123";
        BufferedReader reader = null;

        try {
            URL url = new URL ("http://localhost:8080/v1/protected/students/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Authorization", "Basic "+encodeUsernamePassword(user, password));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonSB = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                jsonSB.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            IOUtils.closeQuietly(reader);
            if(connection != null){
                connection.disconnect();
            }
        }
    }
    private static String encodeUsernamePassword(String user, String password){
        String userPassword = user+":"+password;
        return new String (Base64.encodeBase64(userPassword.getBytes()));
    }
}
