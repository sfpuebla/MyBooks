package com.edu.uoc.mybooks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_VIEW;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Método llamado cuando se recibe un mensaje remoto
     *
     * @param remoteMessage Mensaje recibido de Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Mostrar una notificación al recibir un mensaje de Firebase

        sendNotification(remoteMessage.getNotification().getBody(),
                remoteMessage.getData().get("book_position"));

    }

    /**
     * Crea y muestra una notificación al recibir un mensaje de Firebase
     *
     * @param messageBody Texto a mostrar en la notificación
     */
    private void sendNotification(String messageBody, String bookPosition) {

        Intent intent = new Intent(this, BookListActivity.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra("book_position", bookPosition);
        PendingIntent borrarIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Intent intent2 = new Intent(this, BookListActivity.class);
        intent2.setAction(ACTION_VIEW);
        intent2.putExtra("book_position", bookPosition);
        PendingIntent mostratIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2, 0);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_start_book) // Asignamos un icono
                .setSound(defaultSoundUri) // Notificamos un sonido
                .setLights(Color.BLUE, 5000, 5000) // Iluminación LED
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }) // Vibraciones
                .setContentTitle("Notificación Firebase")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Seleccione una acción que ejecutar con el libro en la posición " +
                    bookPosition + " de la lista de libros existentes." +
                    "\nPuede Eliminar o ver los Detalles del libro indicado."))

                // Añado las acciones con los iconos personalizados
                .addAction(new NotificationCompat.Action(R.drawable.ic_action_eliminar, "Eliminar", borrarIntent))
                .addAction(new NotificationCompat.Action(R.drawable.ic_action_mostrar, "Mostrar", mostratIntent));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}