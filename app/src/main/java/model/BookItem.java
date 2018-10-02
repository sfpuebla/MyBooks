package model;

import java.util.Date;

/**
 * Clase de libro.
 */
public class BookItem {

    // Definición de propiedades
    public Integer identificador;
    public String titulo;
    public String autor;
    public Date fechaPublicacion;
    public String descripcion;
    public String urlImagenPortada;

    // Inicialización de propiedades en el constructor
    public BookItem(Integer identificador,
                    String titulo,
                    String autor,
                    Date fechaPublicacion,
                    String descripcion,
                    String urlImagenPortada) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.descripcion = descripcion;
        this.urlImagenPortada = urlImagenPortada;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
