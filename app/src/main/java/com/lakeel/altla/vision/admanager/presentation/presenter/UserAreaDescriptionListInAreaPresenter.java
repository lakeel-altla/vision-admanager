package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaDescriptionItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListInAreaView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionsByAreaIdUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserAreaDescriptionListInAreaPresenter extends BasePresenter<UserAreaDescriptionListInAreaView> {

    @Inject
    FindUserAreaDescriptionsByAreaIdUseCase findUserAreaDescriptionsByAreaIdUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    Resources resources;

    private static final String ARG_AREA_ID = "areaId";

    private final List<UserAreaDescriptionItemModel> items = new ArrayList<>();

    private String areaId;

    @Inject
    public UserAreaDescriptionListInAreaPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaId = arguments.getString(ARG_AREA_ID, null);
        if (areaId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_ID));
        }

        this.areaId = areaId;
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable1 = findUserAreaDescriptionsByAreaIdUseCase
                .execute(areaId)
                .map(UserAreaDescriptionItemModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable1);

        Disposable disposable2 = findUserAreaUseCase
                .execute(areaId)
                .map(UserAreaModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    String titleFormat = resources.getString(R.string.title_format_user_area_description_list_in_area);
                    String title = String.format(titleFormat, model.name);
                    getView().onUpdateTitle(title);
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                });
        manageDisposable(disposable2);
    }

    public int getItemCount() {
        getLog().d("getItemCount: %d", items.size());

        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        UserAreaDescriptionItemModel model = items.get(position);
        getView().onItemSelected(model.areaDescriptionId);
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaDescriptionItemModel model = items.get(position);
            itemView.onModelUpdated(model);
        }
    }
}
