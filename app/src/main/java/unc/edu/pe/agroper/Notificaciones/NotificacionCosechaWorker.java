package unc.edu.pe.agroper.Notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import javax.xml.transform.Result;

import unc.edu.pe.agroper.R;

public class NotificacionCosechaWorker extends Worker {

    public NotificacionCosechaWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("WORKER_TEST", "Worker ejecutado");

        String nombreCultivo = getInputData().getString("nombre");

        mostrarNotificacion(nombreCultivo);

        return Result.success();
    }

    private void mostrarNotificacion(String nombreCultivo) {

        Context context = getApplicationContext();

        String channelId = "cosecha_channel";

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notificaciones de Cosecha",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("🌾 Cosecha Mañana")
                        .setContentText("El cultivo " + nombreCultivo + " se cosecha mañana.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


}

