package com.edu.uoc.mybooks.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nos basamos en la clase DummyContent para aplicar nuestro propio BookItemContent
 * que utilza la clase BookItem que hemos creado
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BookItemContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<BookItem> ITEMS = new ArrayList<BookItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, BookItem> ITEM_MAP = new HashMap<String, BookItem>();

    public static List<BookItem> getBooks() {
        return BookItem.listAll(BookItem.class);
    }

    public static boolean exists(BookItem bookItem) {
        List<BookItem> bookItems = BookItem.find(BookItem.class,
                "author = ? and description = ? and and title = ? and url_image = ?",
                bookItem.author, bookItem.description, bookItem.title, bookItem.url_image);

        return (!bookItems.isEmpty());
    }

public static void addBook(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.identificador.toString(), item);
    }

}
