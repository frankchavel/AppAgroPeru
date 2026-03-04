package unc.edu.pe.agroper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificacionReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "AgroPer_Tasks";
    private static final String CHANNEL_NAME = "Recordatorios de Tareas";

    @Override
    public void onReceive(Context context, Intent intent) {
        String detalle = intent.getStringExtra("detalle");

        // Si no hay detalle, usa un mensaje por defecto
        if (detalle == null || detalle.isEmpty()) {
            detalle = "Tienes una tarea pendiente";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear el canal de notificación (necesario para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para recordatorios de tareas");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationManager.createNotificationChannel(channel);
        }

        // Asegúrate de tener este icono en tu proyecto
        int iconId = context.getResources().getIdentifier("ic_leaf", "drawable", context.getPackageName());
        if (iconId == 0) {
            iconId = android.R.drawable.ic_dialog_info; // Icono por defecto si no existe ic_leaf
        }

        // Construir la notificación push
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconId)
                .setContentTitle("AgroPer: Tarea Programada")
                .setContentText(detalle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detalle))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL); // Añade sonido y vibración

        // Usar un ID único basado en el tiempo
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}