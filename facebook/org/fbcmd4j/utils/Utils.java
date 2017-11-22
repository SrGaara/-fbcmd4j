package twitter;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
//import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twitter4j.Status;
import twitter4j.User;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Utils {
	static final Logger logger = LogManager.getLogger(Utils.class);

	/* Basado en GetAccessToken.java de 
	 * Yusuke Yamamoto
	 * */
	static void obtenerAccessToken(String foldername, String filename, Properties props, Scanner scanner) {
		try {
		
			Twitter twitter = new TwitterFactory().getInstance();
			/* 1. oauth.consumerKey y oauth.consumerSecret son requeridas para obtener 
			 * los tokens de acceso. Crear condiciones para validar si existen en 
			 * las propiedades props y si no, pediras al usuario para posteriormente guardarlos
			 * con el método props.setProperty("oauth.consumerKey", consumerKey); 
			 * */
			// Escribe tu código aquí {
			/*	Twitter twitter1 = TwitterFactory.getSingleton();
		    twitter1.setOAuthConsumer("[consumer key]", "[consumer secret]");
		    RequestToken requestToken = twitter1.getOAuthRequestToken();
		    AccessToken accessToken = null;
		    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    while (null == accessToken) {
		      System.out.println("Open the following URL and grant access to your account:");
		      System.out.println(requestToken.getAuthorizationURL());
		      System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
		      String pin = br.readLine();
		      try{
		         if(pin.length() > 0){
		           accessToken = twitter1.getOAuthAccessToken(requestToken, pin);
		         }else{
		           accessToken = twitter1.getOAuthAccessToken();
		         }
		      } catch (TwitterException te) {
		        if(401 == te.getStatusCode()){
		          System.out.println("Unable to get the access token.");
		        }else{
		          te.printStackTrace();
		        }
		      }
		    }
		    //persist to the accessToken for future reference.
		    storeAccessToken(twitter1.verifyCredentials().getId() , accessToken);
		    Status status = twitter1.updateStatus(args[0]);
		    System.out.println("Successfully updated the status to [" + status.getText() + "].");
		   System.exit0();}
		  }*/
			
			//esta predefinido
			
			// }

			// Inicia con el proceso de obtener Token
			twitter.setOAuthConsumer(props.getProperty("oauth.consumerKey"), props.getProperty("oauth.consumerSecret"));
			RequestToken requestToken = twitter.getOAuthRequestToken();
			logger.info("Obteninedo request Token.");
			System.out.println("Request token: " + requestToken.getToken());
			System.out.println("Request token secret: " + requestToken.getTokenSecret());
			AccessToken accessToken = null;

			// Obtiene URL para autorizar aplicación
			try {
				while (null == accessToken) {
					System.out.println("\tAbre el siguiente URL y authoriza tu cuenta si no"
							+ " se abre tu navegador automáticamente:");
					System.out.println("\t" +requestToken.getAuthorizationURL());
					try {
						Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
					} catch (UnsupportedOperationException ignore) {
					} catch (IOException ignore) {
					} catch (URISyntaxException e) {
						throw new AssertionError(e);
					}

					// Solicita PIN al usuario
					System.out.print("Ingresa PIN obtenido al autorizar aplicación:");
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

					// Guarda configuración nueva
					saveProperties(foldername, filename, props);
					logger.info("Configuraión guardada exitosamente.");
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
			 * almacenar una expresión lambda para validar si v como String está 
			 * vacío. Si está vacío utilizar logger para informar al usuario.
			 * */
			// Escribe tu código aquí {
			

		
			// }

			/* 3. Utilizar la interfaz funcional creada en el 
			 * método forEach de props
			 * */
			// Escribe tu código aquí {

			// }
		} else {
			logger.info("Creando nuevo archivo de condifugración.");
			Files.copy(Paths.get("twitter","twitter.properties"), configFile);
		}
		return props;
	}

	public static void saveProperties(String foldername, String filename, Properties props) throws IOException{
		Path configFile = Paths.get(foldername, filename);
		props.store(Files.newOutputStream(configFile), "Generado por ObtenerAccessToken");
	}

	public static void printStatus(Object o){
		/* 4. Método para imprimir Status en formato:
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
		// Escribe tu código aquí {
	    Status status = twitter.updateStatus(tweet);
	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
		// }
		return status;
	}
}
