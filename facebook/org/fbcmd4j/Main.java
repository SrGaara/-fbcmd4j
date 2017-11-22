package org.fbcmd4j;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fbcmd4j.utils.Utils;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;
public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	// Recuerda cambiar el path antes de exportarlo sino, el jar requerirá un
	// directorio llamado config para guardar las configuraciones
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "twitter.properties";
	private static final String APP_VERSION = "v1.0";

	public static void main(String[] args) {
		logger.info("inicializando app");
		Twitter twitter = null;
		Properties props = null;
		// Carga propiedades
		try {
			props = Utils.loadPropertiesFromFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {	
			System.out.println(ex);
			logger.error(ex);
		}

		int seleccion;
		try (Scanner scanner = new Scanner(System.in)){
			while(true){
				twitter = Utils.configuraTwitter(props);
				// Inicio Menu
				System.out.format("Simple Twitter client %s\n\n", APP_VERSION);
				System.out.println("Opciones: ");
				System.out.println("(0) Cargar configuracion");
				System.out.println("(1) Obtener Tokens");
				System.out.println("(2) Tweet");
				System.out.println("(3) Leer timeline");
				System.out.println("(4) Info de Usuario");
				System.out.println("(5) Salir");
				System.out.println("\nPor favor ingrese una opción: ");
				// Fin de Menu
				try {
					seleccion = scanner.nextInt();
					scanner.nextLine();

					switch(seleccion){
						case 0:
							props = Utils.loadPropertiesFromFile(CONFIG_DIR, CONFIG_FILE);
							break;
						case 1: 
							Utils.obtenerAccessToken(CONFIG_DIR, CONFIG_FILE, props, scanner);
							props = Utils.loadPropertiesFromFile(CONFIG_DIR, CONFIG_FILE);
							break;
						case 2:
							System.out.println(props);
							System.out.println(twitter);
							System.out.println("Escribe tu tweet: ");
							String tweet = scanner.nextLine();
							Status status = Utils.creaTweet(twitter, tweet);
							System.out.println("Tweet'd [" + status.getText() + "].");
							break;
						case 3:
							ResponseList list = twitter.getHomeTimeline();
							// 1. Crea una referencia al método printStatus para imprimir 
							// cada status
							// Escribe tu codigo aqui {
							//lo comentado no funciono (hace lomismo)
							 /*   Twitter twitter1 = getTwitterinstance();
						     
						    return twitter.getHomeTimeline().stream()
						      .map(item -> item.getText())
						      .collect(Collectors.toList());*/
							Utils.printStatus(list);
							//}
							break;
						case 4:
							System.out.println("Ingresa el usuario: ");
							String userStr = scanner.nextLine();
							// 2. Por medio del usuario ingresado en userStr invocar el método
							// Utils.infoUsuario para imprimir la información
							// Escribe tu codigo aqui {
							Utils.infoUsuario(twitter, userStr);
							//}
							break;
						case 5:
							System.exit(0);
						default:
							logger.error("Opción inválida");
							break;
					}
				} catch (InputMismatchException ex){
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error("Opción inválida. %s. \n", ex.getClass());
					scanner.next();
				} catch (TwitterException ex){
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error(ex.getErrorMessage());
					scanner.next();
				} catch (NoSuchFileException io){
					System.out.println("Archivo de configuración no existe.");
					logger.error(io);
				} catch (Exception ex){
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error(ex);
					scanner.next();
				} 
			}
		} catch (Exception ex){
			logger.error(ex);
		}
	}

}


