package com.lakeel.altla.vision.admanager.presentation.service;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.component.ServiceComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ServiceModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.domain.model.UploadUserActorImageFileTask;
import com.lakeel.altla.vision.domain.usecase.DeleteUploadUserActorImageFileTaskUseCase;
import com.lakeel.altla.vision.domain.usecase.UploadUserActorImageFileUseCase;

import org.parceler.Parcel;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UploadActorImageFileTaskService extends Service {

    private static final Log LOG = LogFactory.getLog(UploadActorImageFileTaskService.class);

    private static final int NOTIFICATION_ID = 1;

    private static final String EXTRA_MODEL = "model";

    @Inject
    UploadUserActorImageFileUseCase uploadUserActorImageFileUseCase;

    @Inject
    DeleteUploadUserActorImageFileTaskUseCase deleteUploadUserActorImageFileTaskUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ServiceComponent serviceComponent;

    private Model model;

    private NotificationManager notificationManager;

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull UploadUserActorImageFileTask task) {
        Model model = new Model();
        model.imageId = task.imageId;
        model.sourceUriString = task.sourceUriString;

        Intent intent = new Intent(context, UploadActorImageFileTaskService.class);
        intent.putExtra(EXTRA_MODEL, Parcels.wrap(model));

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
        model = Parcels.unwrap(intent.getParcelableExtra(EXTRA_MODEL));
        if (model == null) throw new IllegalStateException(String.format("Extra '%s' must be not null.", EXTRA_MODEL));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, MainActivity.createStartIntent(this), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_file_upload_white_24dp)
                .setContentTitle(getString(R.string.notification_upload_title))
                .setContentText(getString(R.string.notification_upload_text))
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Disposable disposable = uploadUserActorImageFileUseCase
                .execute(model.imageId, model.sourceUriString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress -> {
                    // Progress.
                    builder.setProgress((int) progress.totalBytes, (int) progress.bytesTransferred, false);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }, e -> {
                    // Failed.
                    LOG.e("Failed.", e);
                    notificationManager.cancel(NOTIFICATION_ID);
                }, () -> {
                    // Completed.
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

    @Parcel
    public static final class Model {

        public String imageId;

        public String sourceUriString;
    }
}
