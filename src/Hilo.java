import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Hilo extends Thread {

    //! Atributos
    //socket cliente
    private Socket socketCliente;

    //nombre del usuario
    private String nombre;

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

            //invocacion a metodo que registra el nombre de usuario
            registrarNombre();

            //se agrega a si mismo el objeto a la lista de hilos
            this.hilos.add(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Método que registra el nombre de usuario
     */
    public void registrarNombre() {
        try {
            this.salida.write("Por favor, ingresa tu nombre: ");
            this.salida.newLine();
            this.salida.flush();

            //asignamos la primer respuesta del cliente como el nombre
            setNombre(this.entrada.readLine());
        } catch (IOException e) {
            System.out.println("Error al obtener el nombre de un usuario");
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
                System.out.println(this.nombre + ": " + lectura);

                //replicamos el valor recibido en el flujo de entrada
                replicarMensaje(lectura);
            }
        } catch (Exception e) {
            System.out.println("Algo salio mal en la lectura del flujo de entrada...");
            System.exit(1);
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
                    hilo.salida.write(this.nombre + ": " + mensaje);
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

    public Socket getSocketCliente() {
        return socketCliente;
    }

    public void setSocketCliente(Socket socketCliente) {
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


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

