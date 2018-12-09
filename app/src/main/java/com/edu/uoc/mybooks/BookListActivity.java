package com.edu.uoc.mybooks;

import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

// import com.edu.uoc.mybooks.dummy.DummyContent;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

// SugarORM
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_VIEW;
import static android.graphics.Bitmap.Config.ARGB_8888;
import static java.lang.Integer.parseInt;


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
    private DrawerLayout drawerLayout;
    private android.support.v7.app.ActionBar actionBar;
    private boolean mDrawerLayoutDisplayed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Marcamos activity_main.xml como layout de inicio
        // setContentView(R.layout.activity_book_list);
        setContentView(R.layout.activity_main );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (getIntent() != null && getIntent().getAction() != null) {

            String bookPosition = "";
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                if(extras.containsKey("book_position"))
                {
                    bookPosition = getIntent().getExtras().getString("book_position");
                }
            }

            if (getIntent().getAction().equalsIgnoreCase(ACTION_DELETE)) {
               // Acción eliminar
                Toast.makeText(this, "Acción eliminar - BookPosition " + bookPosition, Toast.LENGTH_SHORT).show();

                // Cierro la notificación
                closeNotification();

                Integer pos = Integer.parseInt(bookPosition);
                BookItemContent.deleteBook(pos);

                // Indico que la información de la lista ha cambiado
                View recyclerView = findViewById(R.id.book_list);
                ((RecyclerView) recyclerView).getAdapter().notifyDataSetChanged();

            } else if (getIntent().getAction().equalsIgnoreCase(ACTION_VIEW)) {
                // Acción visualizar
                Toast.makeText(this, "Acción detalle - BookPosition " + bookPosition, Toast.LENGTH_SHORT).show();

                // Cierro la notificación
                closeNotification();

                // Muestro el detalle
                showDetail(bookPosition);
            }
        }

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

        // Obtenemos los datos desde la base de datos Firebase
        GetDataFromFirebase();


        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Volvemos a hacer la petición de los datos
                Toast.makeText(getApplicationContext(), "Recargando datos...", Toast.LENGTH_SHORT).show();
                // Limpiamos el contenido existente
                BookItemContent.clearBooks();
                GetDataFromFirebase();

                swipeContainer.setRefreshing(false);
            }
        });


        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Despliego el navigation drawer si se presiona sobre el botón de las tres rayas en la Toolbar
                if (!mDrawerLayoutDisplayed) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                mDrawerLayoutDisplayed = !mDrawerLayoutDisplayed;

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_share_apps:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                // Envío texto e imágen
                                CompartirTextoImagen();
                                return true;
                            case R.id.item_navigation_drawer_copy_clipboard:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                CopiarTextoPortapapeles();
                                return true;
                            case R.id.item_navigation_drawer_share_whatsapp:
                                menuItem.setChecked(true);
                                CompartirTextoImagenWhatsApp();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
    }

    protected void CompartirTextoImagen() {
        Uri imagenURI = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_user");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_to_share));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imagenURI);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );
        startActivity(Intent.createChooser(shareIntent, "send"));
    }

    protected void CopiarTextoPortapapeles() {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto copiado", getString(R.string.text_to_share));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(BookListActivity.this, "El texto se ha copiado en el portapapeles", Toast.LENGTH_SHORT).show();
    }

    protected void CompartirTextoImagenWhatsApp() {


        // Uri imagenURI = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_user");
        Uri imagenAEnviar = prepararImagen();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_to_share));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imagenAEnviar);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(shareIntent, "send"));


        /*
        // Imagen a través del FileProvider
        File imagePath = new File(BookListActivity.this.getFilesDir(), "drawable");
        File newFile = new File(imagePath, "ic_user");
        Uri contentUri = FileProvider.getUriForFile(BookListActivity.this, getString(R.string.file_provider_authority), newFile);



        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BookListActivity.this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
        */
    }

    private Uri prepararImagen() {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        File imagePath = new File(getFilesDir(), "temporal");
        imagePath.mkdir();
        File imageFile = new File(imagePath.getPath(), "app_icon.png");

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return FileProvider.getUriForFile(getApplicationContext(), getPackageName(), imageFile);
    }

    protected void closeNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }


    protected void showDetail(String bookPosition) {

        Integer pos = Integer.parseInt(bookPosition);

        // Compruebo que no es un índice fuera de nuestra lista
        if (BookItemContent.count() > pos ) {
            // Muestra el detalle de la posición indicada
            RecyclerView recyclerView = findViewById(R.id.book_list);

            Intent intent = new Intent(recyclerView.getContext(), BookDetailActivity.class);
            intent.putExtra(BookDetailFragment.ARG_ITEM_ID, bookPosition);
            startActivity(intent);
        }
    }

    protected void GetDataFromFirebase() {

        // Sólo realizamos la carga desde Firebase si no tenemos datos (o si los hemos borrado previamente)
        if (BookItemContent.count() == 0) {
            // Inicialización de las clases FireBird
            mAuth = FirebaseAuth.getInstance();
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

                                View recyclerView = findViewById(R.id.book_list);

                                // Recorremos las tablas
                                for (DataSnapshot dataRow : dataSnapshot.getChildren()){

                                    // Utilizamos el GenericTypeIndicator
                                    GenericTypeIndicator<ArrayList<BookItem>> t = new GenericTypeIndicator<ArrayList<BookItem>>() {};
                                    ArrayList<BookItem> bookItems = dataRow.getValue(t);

                                    BookItemContent.clearBooks();
                                    for (BookItem item: bookItems) {
                                        BookItemContent.addBook(item);
                                    }

                                    // Aplicamos la lista obtenida
                                    ((SimpleItemRecyclerViewAdapter)((RecyclerView) recyclerView).getAdapter()).setItems(bookItems,
                                            getApplicationContext());
                                }

                                // Indico que la información de la lista ha cambiado
                                ((RecyclerView) recyclerView).getAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                String errorMsg;
                                Integer error = databaseError.getCode();
                                switch (error) {
                                    case DatabaseError.DISCONNECTED:
                                        errorMsg = "Desconexión del servidor";
                                        break;
                                    case DatabaseError.NETWORK_ERROR:
                                        errorMsg = "Error en la red";
                                        break;
                                    case DatabaseError.UNAVAILABLE:
                                        errorMsg = "Servicio no disponible";
                                        break;
                                    default:
                                        errorMsg = "Error en la conexión";
                                        break;
                                }
                                errorMsg += "\n " + databaseError.getMessage();
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                                LoadSavedBooks();
                                Toast.makeText(getApplicationContext(), "Usando datos almacenados", Toast.LENGTH_LONG).show();
                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(), "Error de conexión con Firebase", Toast.LENGTH_LONG).show();

                        LoadSavedBooks();
                        Toast.makeText(getApplicationContext(), "Usando datos almacenados", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }
    }


    protected void LoadSavedBooks() {

        // Incialización de SugarORM
        SugarContext.init(getApplicationContext());

        SchemaGenerator schemaGenerator = new SchemaGenerator(getApplicationContext());
        schemaGenerator.createDatabase(new SugarDb(getApplicationContext()).getDB());

        // Recorremos los registros obtenidos
        List<BookItem> bookItems = BookItemContent.getBooks();

        for (BookItem item: bookItems) {
            BookItemContent.addBook(item);
        }

        SugarContext.terminate();

        // Indico que la información de la lista ha cambiado
        View recyclerView = findViewById(R.id.book_list);
        ((RecyclerView) recyclerView).getAdapter().notifyDataSetChanged();
    }



    protected void SaveBookItem(BookItem bookItem) {

        // Comprobamos si lo tenemos en nuestra lista
        if (!BookItemContent.exists(bookItem)) {
            Toast.makeText(getApplicationContext(), "Dato guardado", Toast.LENGTH_LONG).show();
            // Guardamos el valor en nuestra base de datos local
            bookItem.save();
        }
        else {
            Toast.makeText(getApplicationContext(), "Dato existente", Toast.LENGTH_LONG).show();
        }

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
        private List<BookItem> mValues;
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


        // Actualizamos la lista
        public void setItems(ArrayList<BookItem> items,
                             Context context) {

            /*
            // Incialización de SugarORM
            SugarContext.init(context);

            SchemaGenerator schemaGenerator = new SchemaGenerator(context);
            schemaGenerator.createDatabase(new SugarDb(context).getDB());

            // Recorremos los registros obtenidos
            for (BookItem item: items) {
                // Si no está en nuestra BBDD, guardamos el registro
                if (!BookItemContent.exists(item)) {
                    BookItem.save(item);
                }
            }

            SugarContext.terminate();
            */

            // Cambios indicados desde el curso
            mValues = items;
            notifyDataSetChanged();
        }

        public void deleteItem(Integer position) {

            /*
            if (mValues.size() > position) {
                mValues.remove(position);
            }
            */

            mValues.remove(position);
            notifyDataSetChanged();
        }

        public void showDetail(Integer position, Context context) {

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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Utilizamos las propiedades de nuestra clase BookItem
            holder.mTitleView.setText(mValues.get(position).title);
            // También aplicamos los datos del autor
            holder.mAuthorView.setText(mValues.get(position).author);
            //holder.itemView.setTag(mValues.get(position));

            // Pasamos la posición
            holder.itemView.setTag(position);

            // holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Integer position = (Integer)view.getTag();

                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(BookDetailFragment.ARG_ITEM_ID, position.toString());
                            BookDetailFragment fragment = new BookDetailFragment();
                            fragment.setArguments(arguments);
                            mParentActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.book_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = view.getContext();
                            Intent intent = new Intent(context, BookDetailActivity.class);
                            intent.putExtra(BookDetailFragment.ARG_ITEM_ID, position.toString());
                            context.startActivity(intent);
                        }
                }
            });
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
