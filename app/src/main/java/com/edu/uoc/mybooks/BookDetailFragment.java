package com.edu.uoc.mybooks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// import com.edu.uoc.mybooks.dummy.DummyContent;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.edu.uoc.mybooks.model.BookItem;
import com.edu.uoc.mybooks.model.BookItemContent;

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "book_id";

    /**
     * El contenido del BookItem seleccionado (sustituyendo al dummy item).
     */
    //private DummyContent.DummyItem mItem;
    private BookItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            // Sustituimos el DummyContent por nuestra clase BookItemContent
            mItem = BookItemContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                // Adaptamos a las propiedades de nuestro BookItem
                appBarLayout.setTitle(mItem.title);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        // Mostramos el contenido de nuestro libro
        if (mItem != null) {
            // Adaptamos a las propiedades de nuestro BookItem y añadimos las características del libro
            ((TextView) rootView.findViewById(R.id.txtAutor)).setText(mItem.author);
            ((TextView) rootView.findViewById(R.id.txtDescripcion)).setText(mItem.description);
            // La fecha ya viene en el formato apropiado con los cambios en la clase
            ((TextView) rootView.findViewById(R.id.txtFecha)).setText(mItem.publication_date);

            // Cargamos la imagen del libro
            if (mItem.url_image != null) {
                ImageView image = rootView.findViewById(R.id.imageBook);
                new DownloadImageTask(image).execute(mItem.url_image);
            }
        }

        return rootView;
    }

    // Creamos una tarea en background para que trabaje de manera asíncrona
    // facilitando la carga de los elementos
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        // Objeto ImageView pasado por referencia
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        // Trabajo asíncrono
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        // Finalización de la tarea
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }}
