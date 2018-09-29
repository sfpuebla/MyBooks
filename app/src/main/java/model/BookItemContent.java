package model;

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

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addBook(createDummyBook(i));
        }
    }

    private static void addBook(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.identificador.toString(), item);
    }

    private static BookItem createDummyBook(int position) {
        Date date = new Date();
        // Modificamos la creación para adaptarlo a nuestro objeto BookItem
        return new BookItem(position,"Título " + position, "Autor " + position, date, "Descripción " + position, "URL Imagen Portada " + position );
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Detalles sobre el libro: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMás información aquí.");
        }
        return builder.toString();
    }

}
