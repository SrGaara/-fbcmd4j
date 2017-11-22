package org.fbcmd4j.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.Post;
import facebook4j.internal.org.json.JSONObject;

public class Utils {
	private static final Logger logger = LogManager.getLogger(Utils.class);
	
	/* Basado en GetAccessToken.java de 
	 +	 * Yusuke Yamamoto
	 +	 * */
	// Inicia con el proceso de obtener Token
	 //		twitter.setOAuthConsumer(props.getProperty("oauth.consumerKey"), props.getProperty("oauth.consumerSecret"));
	 //		RequestToken requestToken = twitter.getOAuthRequestToken();
	 //		logger.info("Obteninedo request Token.");
	 //		System.out.println("Request token: " + requestToken.getToken());
	 //		System.out.println("Request token secret: " + requestToken.getTokenSecret());
	 //	AccessToken accessToken = null;
	 
	public static Properties loadConfigFile(String folderName, String fileName) throws IOException {
		Properties props = new Properties();
		Path configFolder = Paths.get(folderName);
		Path configFile = Paths.get(folderName, fileName);
		if (!Files.exists(configFile)) {
			logger.info("Creando nuevo archivo.");
			
			if (!Files.exists(configFolder))
				Files.createDirectory(configFolder);
			
			Files.copy(Utils.class.getResourceAsStream("fbcmd4j.properties"), configFile);
		}

		props.load(Files.newInputStream(configFile));
		BiConsumer<Object, Object> emptyProperty = (k, v) -> {
			if(((String)v).isEmpty())
				logger.info("La propiedad '" + k + "' esta vacio");
		};
		props.forEach(emptyProperty);

		return props;
	}
	
	public static void estabtoken(String folderName, String fileName, Properties propiedades, Scanner scanner) {
		try {
			//redirect_uri es un parámetro opcional. Cuando proporcionas una URL,
			//se redirigirá a la persona a esta URL después de completar el inicio de sesión correctamente. 
			// Esto te permite iniciar la sesión de la persona en el sitio web de tu aplicación para fines de una administración de cuentas más completa
			//Esta URL debe ser una URL de redireccionamiento de OAuth
			// se configura en "Configuración > Avanzada"
			URL url = new URL("https://graph.facebook.com/v2.6/device/login");
	        Map<String,Object> params = new LinkedHashMap<>();
	        //access_token=<YOUR_APP_ID|CLIENT_TOKEN>
	        params.put("access_token", "122183835131175|8773a08f5430ce908f7a2a1ba51dfcfd");
	        //El parámetro scope es opcional y debe contener una lista separada por comas
	        //de permisos de inicio de sesión aprobados para su uso en la revisión del inicio de sesión.
	        params.put("scope", propiedades.getProperty("oauth.permissions"));

	        StringBuilder postData = new StringBuilder();
	        for (Map.Entry<String,Object> param : params.entrySet()) {
	            if (postData.length() != 0) postData.append('&');
	            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
	            postData.append('=');
	            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	        }
	        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	        conn.setDoOutput(true);
	        conn.getOutputStream().write(postDataBytes);

	        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        for (int c; (c = in.read()) >= 0;)
	            sb.append((char)c);
	        String response = sb.toString();
	        
	        JSONObject obj = new JSONObject(response);
	        String code = obj.getString("code");
	        String userCode = obj.getString("user_code");
	        
			System.out.println("Ingresa a la pagina https://www.facebook.com/device , logea en face book, acepta los permisos, inserta la siguiente clave --->: " + userCode);

			String accessToken = "";
			while(accessToken.isEmpty()) {
		        try {
		            TimeUnit.SECONDS.sleep(5);
		        } catch (InterruptedException e) {
					logger.error(e);
		        }
		        
		        //Consultar la autorización
		        
		        URL url1 = new URL("https://graph.facebook.com/v2.6/device/login_status");
		        params = new LinkedHashMap<>();
		        params.put("access_token", "122183835131175|8773a08f5430ce908f7a2a1ba51dfcfd");
		        params.put("code", code);    
		        postData = new StringBuilder();
		        for (Map.Entry<String,Object> param : params.entrySet()) {
		            if (postData.length() != 0) postData.append('&');
		            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
		            postData.append('=');
		            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		        }
		        postDataBytes = postData.toString().getBytes("UTF-8");
	
		        HttpURLConnection conn1 = (HttpURLConnection)url1.openConnection();
		        conn1.setRequestMethod("POST");
		        conn1.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		        conn1.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		        conn1.setDoOutput(true);
		        conn1.getOutputStream().write(postDataBytes);

		        try {
		        	in = new BufferedReader(new InputStreamReader(conn1.getInputStream(), "UTF-8"));
			        sb = new StringBuilder();
			        for (int c; (c = in.read()) >= 0;)
			            sb.append((char)c);		        
			        response = sb.toString();
			        
			        obj = new JSONObject(response);
			        accessToken = obj.getString("access_token");
		        } catch(IOException ignore) {
		        }
		    }
			
	        propiedades.setProperty("oauth.accessToken", accessToken);
	        
			saveProperties(folderName, fileName, propiedades);
			System.out.println("has iniciado sesion.");
			logger.info("has iniciado sesion.");
		} catch(Exception e) {
			logger.error(e);
		}
		
		
		//URL url = new URL("https://graph.facebook.com/v2.6/device/login");
		  //     int scope;
			//int redirect_uri;
			//Object access_token = 122183835131175|8773a08f5430ce908f7a2a1ba51dfcfd>
		      // scope=<user_website, user_about_me, user_status, user_posts, publish_actions, public_profile> // e.g. public_profile,user_likes
		       //redirect_uri=<VALID_OAUTH_REDIRECT_URL>
	}
	
	public static void postLink(String link, Facebook facebook) {
		try {
			//facebook.postLink(new URL("http://facebook4j.org"));
			//facebook.postLink(new URL("http://facebook4j.org"), "A Java library for the Facebook Graph API");
			System.out.println("Se ha publicado la URL");
			facebook.postLink(new URL(link));
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (FacebookException e) {
			logger.error(e);
		}
	}
	
	public static void saveProperties(String folderName, String fileName, Properties props) throws IOException {
		Path configFile = Paths.get(folderName, fileName);
		props.store(Files.newOutputStream(configFile), "Generado por org.fbcmd4j.configTokens");
	}

	public static void postStatus(String mensage, Facebook facebook) {
		try {
			System.out.println("Se ha publicado el estado");
			facebook.postStatusMessage(mensage);
		} catch (FacebookException e) {
			logger.error(e);
		}		
	}
	
	public static Facebook configFacebook(Properties props) {
		Facebook fb = new FacebookFactory().getInstance();
		fb.setOAuthAppId(props.getProperty("oauth.appId"), props.getProperty("oauth.appSecret"));
		fb.setOAuthPermissions(props.getProperty("oauth.permissions"));
		if(props.getProperty("oauth.accessToken") != null)
			fb.setOAuthAccessToken(new AccessToken(props.getProperty("oauth.accessToken"), null));
		
		return fb;
	}
	public static void printPost(Post p) {
		if(p.getStory() != null)
			System.out.println("Historia: " + p.getStory());
		if(p.getMessage() != null)
			System.out.println("publicacion: " + p.getMessage());
		System.out.println("--------------------------------");
	}
	
	
	public static String savePostsToFile(String fileName, List<Post> posts) {
		File file = new File(fileName + ".txt");

		try {
    		if(!file.exists()) {
    			file.createNewFile();
            }

    		FileOutputStream fos = new FileOutputStream(file);
			for (Post p : posts) {
				String mensaje = "";
				if(p.getStory() != null)
					mensaje += "Story: " + p.getStory() + "\n";
				if(p.getMessage() != null)
					mensaje += "Mensaje: " + p.getMessage() + "\n";
				mensaje += "--------------------------------\n";
				fos.write(mensaje.getBytes());
			}
			fos.close();

			logger.info("Posts guardados en el archivo '" + file.getName() + "'.");
			System.out.println("el post se guardo en '" + file.getName() + "'.");
		} catch (IOException e) {
			logger.error(e);
		}
        
        return file.getName();
       // return status;
	}	
}