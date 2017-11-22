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
			//redirect_uri es un par·metro opcional. Cuando proporcionas una URL,
			//se redirigir· a la persona a esta URL despuÈs de completar el inicio de sesiÛn correctamente. 
			// Esto te permite iniciar la sesiÛn de la persona en el sitio web de tu aplicaciÛn para fines de una administraciÛn de cuentas m·s completa
			//Esta URL debe ser una URL de redireccionamiento de OAuth
			// se configura en "ConfiguraciÛn > Avanzada"
			URL url = new URL("https://graph.facebook.com/v2.6/device/login");
	        Map<String,Object> params = new LinkedHashMap<>();
	        //access_token=<YOUR_APP_ID|CLIENT_TOKEN>
	        params.put("access_token", "122183835131175|8773a08f5430ce908f7a2a1ba51dfcfd");
	        //El par·metro scope es opcional y debe contener una lista separada por comas
	        //de permisos de inicio de sesiÛn aprobados para su uso en la revisiÛn del inicio de sesiÛn.
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
		        
		        //Consultar la autorizaciÛn
		        
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
	
	

					// Solicita PIN al usuario
					System.out.print("Ingresa PIN obtenido al autorizar aplicaci√≥n:");
					String pin = scanner.nextLine();

					try {
						if (pin.length() > 0) {
							accessToken = twitter.getOAuthAccessToken(requestToken, pin);
						} else {
							accessToken = twitter.getOAuthAccessToken(requestToken);
						}
					} catch (TwitterException te) {
						logger.error(te);
					}

					logger.info("Access token Obtenido.");
					System.out.println("\tAccess token: " + accessToken.getToken());
					System.out.println("\tAccess token secret: " + accessToken.getTokenSecret());
					props.setProperty("oauth.accessToken", accessToken.getToken());
					props.setProperty("oauth.accessTokenSecret", accessToken.getTokenSecret());

					// Guarda configuraci√≥n nueva
					saveProperties(foldername, filename, props);
					logger.info("Configurai√≥n guardada exitosamente.");
				}
			} catch(Exception e){
				logger.error(e);
			}
		} catch(TwitterException e){
			logger.error(e);
		}
	}

	public static Twitter configuraTwitter(Properties props){
		Twitter twitter = new TwitterFactory().getInstance();
		logger.info("Configurando Instancia de twitter");
		twitter.setOAuthConsumer(props.getProperty("oauth.consumerKey"), 
				props.getProperty("oauth.consumerSecret"));
		if (!(props.getProperty("oauth.accessTokenSecret") ==  null) && 
				!(props.getProperty("oauth.accessTokenSecret") ==  null))
			twitter.setOAuthAccessToken(new AccessToken(props.getProperty("oauth.accessToken"),
					props.getProperty("oauth.accessTokenSecret")));
		return twitter;
	}

	public static Properties loadPropertiesFromFile(String foldername, String filename) throws IOException{
		Properties props = new Properties();
		Path configFile = Paths.get(foldername, filename);
		if (Files.exists(configFile)){
			props.load(Files.newInputStream(configFile));
			/* 2. Definir una interfaz funcional predeterminada (java.util.function) y 
			 * almacenar una expresi√≥n lambda para validar si v como String est√° 
			 * vac√≠o. Si est√° vac√≠o utilizar logger para informar al usuario.
			 * */
			// Escribe tu c√≥digo aqu√≠ {
			

		
			// }

			/* 3. Utilizar la interfaz funcional creada en el 
			 * m√©todo forEach de props
			 * */
			// Escribe tu c√≥digo aqu√≠ {

			// }
		} else {
			logger.info("Creando nuevo archivo de condifugraci√≥n.");
			Files.copy(Paths.get("twitter","twitter.properties"), configFile);
		}
		return props;
	}

	public static void saveProperties(String foldername, String filename, Properties props) throws IOException{
		Path configFile = Paths.get(foldername, filename);
		props.store(Files.newOutputStream(configFile), "Generado por ObtenerAccessToken");
	}

	public static void printStatus(Object o){
		/* 4. M√©todo para imprimir Status en formato:
		 *    Enviado por: @ status.getUser().getScreenName() + status.getText()
		 * */
		  @SuppressWarnings("unchecked")
		List<Status> statuses = (List<Status>) o;
		    System.out.println("Showing home timeline.");
		    for (Status status : statuses) {
		        System.out.println(status.getUser().getName() + ":" +
		                           status.getText());
		    }
		    //by>=:http://twitter4j.org/en/code-examples.html
		
	}

	public static void infoUsuario(Twitter twitter, String userStr) throws TwitterException {
		User user = twitter.showUser(userStr);
		if (user.getStatus() != null) {
			System.out.println("\n@" + user.getScreenName() + " - " + user.getStatus().getText());
		} else {
			System.out.println("\n@" + user.getScreenName() + " es restringido.");
		}
	}

	// Funcion para publicar un tweet
	static Status creaTweet(Twitter twitter, String tweet) throws TwitterException{
		/* 5. Publica un estado en twitter en base al texto de entrada
		 *    regresa el estado creado.
		 * */
		// Escribe tu c√≥digo aqu√≠ {
	    Status status = twitter.updateStatus(tweet);
	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
		// }
		return status;
	}
}
