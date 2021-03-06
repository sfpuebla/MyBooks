package com.edu.uoc.mybooks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// import com.edu.uoc.mybooks.dummy.DummyContent;

import java.util.List;

import com.edu.uoc.mybooks.model.BookItem;
import com.edu.uoc.mybooks.model.BookItemContent;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            /*
            ((BookDetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.book_list))
                    .setActivateOnItemClick(true);
             */
        }

        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        // Sustituyo la clase DummyContent por nuestra clase BookItemContent
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, BookItemContent.ITEMS, mTwoPane));
    }

    // Adaptamos la clase SimpleItemRecyclerViewApapter para que utilice BookItems
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        Integer mRowCount = 0;

        private final BookListActivity  mParentActivity;
        // Cambio la lista de DummyContent.DummyItem por nuestra clase BookItem
        private final List<BookItem> mValues;
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Modificamos el cast de la clase a nuestra clase BookItem
                // DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                BookItem item = (BookItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    // La propiedad id para a ser identificador
                    //arguments.putString(BookDetailFragment.ARG_ITEM_ID, item.id);
                    arguments.putString(BookDetailFragment.ARG_ITEM_ID, item.identificador.toString());
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.book_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    // La propiedad id para a ser identificador
                    // intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.id);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.identificador.toString());

                    context.startActivity(intent);
                }
            }
        };

        // Modifico la creación de la clase para que utilice BookItem
        SimpleItemRecyclerViewAdapter(BookListActivity parent,
                                      List<BookItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view;

            mRowCount += 1;

            // Compruebo si el número de fila es par o impar
            if ((mRowCount & 1) == 0) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list_content_par, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list_content, parent, false);

            }

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // Utilizamos las propiedades de nuestra clase BookItem
            // holder.mIdView.setText(mValues.get(position).id);
            // holder.mContentView.setText(mValues.get(position).content);
            // holder.mIdView.setText(mValues.get(position).identificador.toString());
            holder.mTitleView.setText(mValues.get(position).titulo);

            // También aplicamos los datos del autor
            holder.mAuthorView.setText(mValues.get(position).autor);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

            // Aplicamos un color de ejemplo
            // holder.itemView.setBackgroundColor(Color.RED);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        // Representa la clase donde se previsualiza los elementos de cada libro en la lista
        class ViewHolder extends RecyclerView.ViewHolder {
            // final TextView mIdView;
            final TextView mTitleView;
            // Control TextView asociado al autor
            final TextView mAuthorView;

            ViewHolder(View view) {
                super(view);
                //mIdView = (TextView) view.findViewById(R.id.id_text);
                mTitleView = (TextView) view.findViewById(R.id.title);
                // Asignamos el control del autor al elemento del Layout
                mAuthorView = (TextView) view.findViewById(R.id.author);
            }
        }
    }
}
