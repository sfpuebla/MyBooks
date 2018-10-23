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
import android.widget.Toast;

// import com.edu.uoc.mybooks.dummy.DummyContent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.edu.uoc.mybooks.model.BookItem;
import com.edu.uoc.mybooks.model.BookItemContent;

// Clases para la conexión con la base de datos Firebase
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
    private String STR_FIREBASE_URL = "https://mybooks-a6393.firebaseio.com/";
    private String STR_FIREBASE_CHILD = "test";


    private boolean mTwoPane;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mReference;



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

        /* FileInputStream serviceAccount = new FileInputStream("service_account_key/mybooks-a6393-firebase-adminsdk-pk0dt-0ce4fcf03d.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://mybooks-a6393.firebaseio.com")
                .build();
        */

        // FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        // Inicialización de las clases FireBird
        database = FirebaseDatabase.getInstance();

        // Registro con el usuario y contraseña
        String STR_FIREBASE_USER_EMAIL = "sfpuebla@gmail.com";
        String STR_FIREBASE_USER_PWD = "123456";


        // sfernandezpuebla@uoc.edu
        // 123456
        mAuth.signInWithEmailAndPassword(STR_FIREBASE_USER_EMAIL, STR_FIREBASE_USER_PWD).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Conexión establecida con Firebase", Toast.LENGTH_LONG).show();

                    // Obtenemos una instancia de DatabaseReference
                    mReference = database.getReference();
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // Recorremos las tablas
                            for (DataSnapshot dataRow : dataSnapshot.getChildren()){

                                // Recorremos los registros
                                for(DataSnapshot data : dataRow.getChildren()){

                                    String key = data.getKey().toString();

                                    String author = data.child("author").getValue().toString();
                                    String description = data.child("description").getValue().toString();
                                    String publication_date = data.child("publication_date").getValue().toString();
                                    String title = data.child("title").getValue().toString();
                                    String url_image = data.child("url_image").getValue().toString();

                                    Date date1= null;
                                    try {
                                        date1 = new SimpleDateFormat("dd/MM/yyyy").parse(publication_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    BookItem book = new BookItem( Integer.parseInt(key),
                                            author,
                                            description,
                                            date1,
                                            title,
                                            url_image);

                                    BookItemContent.addBook(book);

                                    // Comprobamos si lo tenemos en nuestra lista
                                    if (!BookItemContent.exists(book)) {
                                        // Guardamos el valor en nuestra base de datos local
                                        book.save();
                                   }

                                }
                           }

                            // Indico que la información de la lista ha cambiado
                            View recyclerView = findViewById(R.id.book_list);
                            ((RecyclerView) recyclerView).getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Conexión con Firebase cancelada: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), "Error de conexión con Firebase", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected void onStart() {
        super.onStart();

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
        public final List<BookItem> mValues;
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
            holder.mTitleView.setText(mValues.get(position).title);

            // También aplicamos los datos del autor
            holder.mAuthorView.setText(mValues.get(position).author);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
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
