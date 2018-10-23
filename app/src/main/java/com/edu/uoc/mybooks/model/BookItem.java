package com.edu.uoc.mybooks.model;

import java.util.Date;

/**
 * Clase de libro.
 */
public class BookItem {

    // Definición de propiedades
    public Integer identificador;
    public String author;
    public String description;
    public Date publication_date;
    public String title;
    public String url_image;

    // Inicialización de propiedades en el constructor
    public BookItem(Integer identificador,
                    String author,
                    String description,
                    Date publication_date,
                    String title,
                    String url_image) {
        this.identificador = identificador;
        this.title = title;
        this.author = author;
        this.publication_date = publication_date;
        this.description = description;
        this.url_image = url_image;
    }

    @Override
    public String toString() {
        return title;
    }
}



