package com.lakeel.altla.vision.admanager.presentation.service;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.component.ServiceComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ServiceModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.domain.model.ImageAssetFileUploadTask;

import org.parceler.Parcels;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetFileUploadTaskService extends Service {

    private static final Log LOG = LogFactory.getLog(UserImageAssetFileUploadTaskService.class);

    private static final int NOTIFICATION_ID = 1;

    private static final String EXTRA_TASK = "task";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ServiceComponent serviceComponent;

    private ImageAssetFileUploadTask task;

    private NotificationManager notificationManager;

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull ImageAssetFileUploadTask task) {
        Intent intent = new Intent(context, UserImageAssetFileUploadTaskService.class);
        intent.putExtra(EXTRA_TASK, Parcels.wrap(task));
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        serviceComponent = MyApplication.getApplicationComponent(this)
                                        .serviceComponent(new ServiceModule(this));
        serviceComponent.inject(this);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task = Parcels.unwrap(intent.getParcelableExtra(EXTRA_TASK));
        if (task == null) throw new IllegalStateException(String.format("Extra '%s' must be not null.", EXTRA_TASK));
        if (task.getSourceUriString() == null) throw new IllegalStateException("sourceUriString must be not null.");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, MainActivity.createStartIntent(this), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_file_upload_white_24dp)
                .setContentTitle(getString(R.string.notification_upload_title))
                .setContentText(getString(R.string.notification_upload_text))
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Disposable disposable = Completable
                .create(e -> {
                    visionService.getUserAssetApi().uploadUserImageAssetFile(task, aVoid -> {
                        e.onComplete();
                    }, e::onError, (totalBytes, bytesTransferred) -> {
                        // Progress.
                        builder.setProgress((int) totalBytes, (int) bytesTransferred, false);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    });
                })
                .subscribe(() -> {
                    notificationManager.cancel(NOTIFICATION_ID);
                }, e -> {
                    // Failed.
                    LOG.e("Failed.", e);
                    notificationManager.cancel(NOTIFICATION_ID);
                });
        compositeDisposable.add(disposable);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
