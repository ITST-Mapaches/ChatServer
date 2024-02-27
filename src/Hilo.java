import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Hilo extends Thread {

    //! Atributos
    //socket cliente
    private Socket socketCliente;
    //para flujo de entrada del socket
    private BufferedReader entrada;
    //para flujo de salida del socket
    private BufferedWriter salida;

    //lista que contendrá objetos de tipo hilo, que en su interior también en un socket cliente
    private static ArrayList<Hilo> hilos = new ArrayList<Hilo>();

    /**
     * Constructor
     *
     * @param socket referencia a socket
     */
    public Hilo(Socket socketCliente) {
        this.socketCliente = socketCliente;

        try {
            //permite leer información a través del socket
            this.entrada = new BufferedReader(new InputStreamReader(this.socketCliente.getInputStream()));

            //permite escribir información
            this.salida = new BufferedWriter(new OutputStreamWriter(this.socketCliente.getOutputStream()));

            //se agrega a si mismo el objeto a la lista de hilos
            this.hilos.add(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Método usado para leer información del flujo de entrada
     */
    public void recibirMensaje() {
        try {
            //variable donde se guardará la lectura del flujo de entrada
            String lectura;

            while (true) {
                //leemos el flujo de entrada y guardamos en lectura
                lectura = this.entrada.readLine();

                //imprimimos en consola la entrada
                System.out.println(lectura);

                //replicamos el valor recibido en el flujo de entrada
                replicarMensaje(lectura);
            }
        } catch (Exception e) {
            System.out.println("Algo salio mal en la lectura del flujo de entrada...");
        }
    }


    /**
     * Método que replica la lectura del flujo de entrada a todos los hilos
     *
     * @param mensaje a replicar
     */
    public void replicarMensaje(String mensaje) {
        //recorremos cada hilo de la lista hilos
        for (Hilo hilo : hilos) {

            //evaluamos si el hilo actual es diferente al hilo this
            if (!this.equals(hilo)) {
                try {
                    //replicamos el mensaje recibido

                    //almacena el mensaje en el buffer
                    hilo.salida.write(mensaje);
                    //salto de línea
                    hilo.salida.newLine();
                    //envía el mensaje a través del flujo de salida
                    hilo.salida.flush();
                } catch (IOException e) {

                    System.out.println("Algo salio mal al intentar replicar el mensaje...");
                }
            }
        }
    }

    /**
     * Sobreescritura de método run de la clase Thread
     * este método está constantemente ejecutandose
     */
    @Override
    public void run() {
        //siempre va a estar a la escucha de lo que se reciba en el flujo de entrada
        recibirMensaje();
    }


    //metodos de acceso (get y set)
    public Socket getSocket() {
        return socketCliente;
    }

    public void setSocket(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    public BufferedReader getEntrada() {
        return entrada;
    }

    public void setEntrada(BufferedReader entrada) {
        this.entrada = entrada;
    }

    public BufferedWriter getSalida() {
        return salida;
    }

    public void setSalida(BufferedWriter salida) {
        this.salida = salida;
    }

    public static ArrayList<Hilo> getHilos() {
        return hilos;
    }

    public static void setHilos(ArrayList<Hilo> hilos) {
        Hilo.hilos = hilos;
    }

    /**
     * Sobreescritura de metodo equals, permite identificar si un objeto es igual al actual (this)
     *
     * @param o objeto a comparar
     * @return igualdad
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hilo)) return false;
        Hilo hilo = (Hilo) o;
        return Objects.equals(socketCliente, hilo.socketCliente) && Objects.equals(entrada, hilo.entrada) && Objects.equals(salida, hilo.salida);
    }

    /**
     * Sobreescritura de metodo hashcode, gener un hash único a partir de los atributos del objeto actual
     * permite identificar si un objeto es igual a otro por medio de su hash
     *
     * @return hash del objeto
     */
    @Override
    public int hashCode() {
        return Objects.hash(socketCliente, entrada, salida);
    }
}

