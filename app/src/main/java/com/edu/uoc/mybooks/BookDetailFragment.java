package com.edu.uoc.mybooks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
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

import model.BookItem;
import model.BookItemContent;

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
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
             mItem = BookItemContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            // int index = Integer.valueOf(getArguments().getString(ARG_ITEM_ID));
            // mItem=(BookItem) BookItemContent.ITEMS.get(index);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                // Adaptamos a las propiedades de nuestro BookItem
                // appBarLayout.setTitle(mItem.content);
                appBarLayout.setTitle(mItem.titulo);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            // Adaptamos a las propiedades de nuestro BookItem
             // ((TextView) rootView.findViewById(R.id.book_detail)).setText(mItem.autor);
            //((TextView) rootView.findViewById(R.id.author)).setText(mItem.autor);
//            ((ImageView) rootView.findViewById(R.id.imageBook)).setImageURI(Uri.parse("https://www.creativosonline.org/blog/wp-content/uploads/2010/04/creativos_online_portadas_libros_inspiracion.png"));

            // ((TextView) rootView.findViewById(R.id.book_detail)).setText(mItem.titulo);

            ((TextView) rootView.findViewById(R.id.txtAutor)).setText(mItem.autor);
            ((TextView) rootView.findViewById(R.id.txtFecha)).setText(mItem.fechaPublicacion.toString());
            ((TextView) rootView.findViewById(R.id.txtDescripcion)).setText(mItem.descripcion);

            if (mItem.urlImagenPortada != null) {
                ImageView image = rootView.findViewById(R.id.imageBook);
                new DownloadImageTask(image).execute(mItem.urlImagenPortada);
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
