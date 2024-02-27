import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    public static void main(String[] x) {

        //en caso de que no se reciba un parametro en tiempo de ejecución
        if (x.length != 1) {
            System.out.println("Se debe especificar un unico valor que represente" +
                    "el numero del puerto");

            //termina el programa
            System.exit(1);
        }

        try {
            //castea el argumento recibido en tiempo de ejecución y lo almacena como puerto
            int puerto = Integer.parseInt(x[0]);

            //crea un socket de tipo servidor
            ServerSocket server = new ServerSocket(puerto);

            while (true) {
                //creamos un socket cliente cuando se reciba una conexión y sea aceptada
                Socket socketClient = server.accept();

                //crea un nuevo hilo, enviando la referencia a un nuevo socket cliente
                //además start() inicia el hijo de ejecución que a su vez permite la ejecución del método run()
                new Hilo(socketClient).start();
            }

        } catch (IOException e) {
            System.out.println("Ha ocurrido un error...");
        }
    }
}
