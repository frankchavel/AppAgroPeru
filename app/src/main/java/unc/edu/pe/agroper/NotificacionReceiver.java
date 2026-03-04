package unc.edu.pe.agroper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificacionReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "agroper_channel_01";
    private static final String CHANNEL_NAME = "AgroPer Notificaciones";
    private static final String TAG = "NotificacionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "=== NOTIFICACIÓN RECIBIDA ===");
        Log.d(TAG, "Timestamp actual: " + System.currentTimeMillis());

        // Adquirir wakelock
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "AgroPer:NotificacionWakelock"
        );
        wakeLock.acquire(5000); // 5 segundos

        try {
            String detalle = intent.getStringExtra("detalle");
            String actividad = intent.getStringExtra("actividad");
            String cultivo = intent.getStringExtra("cultivo");

            if (detalle == null || detalle.isEmpty()) {
                detalle = "Tienes una tarea pendiente";
            }

            int recordatorioId = intent.getIntExtra("recordatorioId", (int) System.currentTimeMillis());

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Crear canal de notificación
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Canal para recordatorios de tareas");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 500, 200, 500});
                channel.setShowBadge(true);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                channel.enableLights(true);
                channel.setLightColor(0xFF39FF14);
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Canal de notificación creado/verificado");
            }

            // Intent para abrir la app
            Intent intentApp = new Intent(context, CalendarioActivity.class);
            intentApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intentApp,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Icono
            int iconId = context.getResources().getIdentifier("ic_leaf", "drawable", context.getPackageName());
            if (iconId == 0) {
                iconId = android.R.drawable.ic_dialog_info;
            }

            // Título personalizado según actividad
            String titulo = "🌱 AgroPer: " + (actividad != null ? actividad : "Recordatorio");

            // Construir notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(iconId)
                    .setContentTitle(titulo)
                    .setContentText(detalle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(detalle))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            // Notificar
            notificationManager.notify(recordatorioId, builder.build());
            Log.d(TAG, "Notificación enviada con ID: " + recordatorioId);

        } catch (Exception e) {
            Log.e(TAG, "Error en onReceive: " + e.getMessage(), e);
        } finally {
            wakeLock.release();
        }
    }
}