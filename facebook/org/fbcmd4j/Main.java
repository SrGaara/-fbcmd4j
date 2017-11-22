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
//referencias
//http://facebook4j.github.io/en/code-examples.html#post_link
//https://unpocodejava.com/2014/10/07/un-poco-de-facebook4j-facebook-java/
//https://github.com/jm66/CS13303/commit/3ee097c0bf67cf0d1a890437b0419a612e2f7f0b
//el archivo log se ha dejado en blanco ya que asi lo requiere la evidencia




public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	// Recuerda cambiar el path antes de exportarlo sino, el jar requerirÃ¡ un
	// directorio llamado config para guardar las configuraciones
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";
	private static final String APP_VERSION = "v1.0";
	logger.info("Iniciando app");
	Facebook facebook =  null;
	Properties props = null;

	try {
		props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
	} catch (IOException ex) {
		System.out.println(ex);
			logger.error(ex);
	}
	
	int seleccion = 1;
	try {
		Scanner scan = new Scanner(System.in);
		while(true) {
			facebook = Utils.configFacebook(props);
			//System.out.println("facebook en linea de comandos #Arturo estuvo aqui\n\n"+  "Opciones: \n"+  "(1) NewsFeed \n"+  "(2) Wall \n"+  "(3) Publicar Estado \n"+  "(4) Publicar Link \n"+  "(5) Salir \n"+  "\nPor favor ingrese una opciÃ³n:");
			System.out.format("Simple facebook client   #Arturo estuvo aqui %s\n\n",APP_VERSION);
			System.out.println("!!!en este programa se pone en practica!!!!!! ");
			System.out.println("!!!lo aprendido en el curso de computacion!!! ");
			System.out.println("!!!en java. este programa te premitira!!!!!!! ");
			System.out.println("!!!realizar las operaciones que !!!!!!!!!!!!! ");
			System.out.println("!!!aparecen a continuacion!!!!!!!!!!!!!!!!!!! \n");
			System.out.println(" \n");
			System.out.println("!!!favor de loguearse primero (opcion 0)!!!!! \n");
			System.out.println("Opciones: ");
			System.out.println("(0) Cargar configuracion");
			System.out.println("(1) mostrar newsfeed");
			System.out.println("(2) ver el muro");
			System.out.println("(3) publicar estado");
			System.out.println("(4) publicar una URL");
			System.out.println("(5) Salir");
			System.out.println("\nIngrese una opcion: ");
			try {
				seleccion = scan.nextInt();
				scan.nextLine();
				switch (seleccion) {
				case 0:
					Utils.estabtoken(CONFIG_DIR, CONFIG_FILE, props, scan);
					props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
					break;
				case 1:
					System.out.println("Mostrando noticias...");
					ResponseList<Post> newsFeed = facebook.getFeed();
					for (Post p : newsFeed) {
						Utils.printPost(p);
					}
		
					break;
				case 2:
					System.out.println("Mostrando el muro...");
					ResponseList<Post> wall = facebook.getPosts();
					for (Post p : wall) {
						Utils.printPost(p);
					}		
		
					break;
				case 3:
					System.out.println("que estas pensando?");
					String estado = scan.nextLine();
					Utils.postStatus(estado, facebook);
					break;
				case 4:
					System.out.println("Ingresa el URL a publicar: ");
					String link = scan.nextLine();
					Utils.postLink(link, facebook);
					break;
				case 5:
					System.out.println("bye bye");
					System.exit(0);
					break;
				default:
					break;
				}
			} catch (InputMismatchException ex) {
				System.out.println("Ocurrió un errror, favor de revisar log o no tienes internet.");
				 logger.error("Opción inválida. %s. \n", ex.getClass());
			} catch (FacebookException ex) {
				System.out.println("Ocurrió un errror, favor de revisar log. o no tienes internet");
				logger.error(ex.getErrorMessage());
			} catch (Exception ex) {
				System.out.println("Ocurrió un errror, favor de revisar log.");
				logger.error(ex);
			}
			System.out.println();
		}
	} catch (Exception e) {
		logger.error(e);
	}
}


}
