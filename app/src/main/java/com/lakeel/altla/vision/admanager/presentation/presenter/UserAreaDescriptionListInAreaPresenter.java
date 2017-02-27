package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListInAreaView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserAreaDescriptionsByAreaIdUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserAreaDescriptionListInAreaPresenter extends BasePresenter<UserAreaDescriptionListInAreaView>
        implements DataList.OnItemListener {

    @Inject
    ObserveUserAreaDescriptionsByAreaIdUseCase observeUserAreaDescriptionsByAreaIdUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    Resources resources;

    private static final String ARG_AREA_ID = "areaId";

    private final DataList<ItemModel> items = new DataList<>(this);

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
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable1 = observeUserAreaDescriptionsByAreaIdUseCase
                .execute(areaId)
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.change(model.type, model.item, model.previousId);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable1);

        Disposable disposable2 = findUserAreaUseCase
                .execute(areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userArea -> {
                    String titleFormat = resources.getString(R.string.title_format_user_area_description_list_in_area);
                    String title = String.format(titleFormat, userArea.name);
                    getView().onUpdateTitle(title);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable2);
    }

    @Override
    public void onItemInserted(int index) {
        getView().onItemInserted(index);
    }

    @Override
    public void onItemChanged(int index) {
        getView().onItemChanged(index);
    }

    @Override
    public void onItemRemoved(int index) {
        getView().onItemRemoved(index);
    }

    @Override
    public void onItemMoved(int from, int to) {
        getView().onItemMoved(from, to);
    }

    @Override
    public void onDataSetChanged() {
        getView().onDataSetChanged();
    }

    public int getItemCount() {
        getLog().d("getItemCount: %d", items.size());

        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        ItemModel model = items.get(position);
        getView().onItemSelected(model.areaDescriptionId);
    }

    @NonNull
    private EventModel map(@NonNull DataListEvent<UserAreaDescription> event) {
        EventModel model = new EventModel();
        model.type = event.getType();
        model.item = map(event.getData());
        model.previousId = event.getPreviousChildName();
        return model;
    }

    @NonNull
    private ItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        ItemModel model = new ItemModel();
        model.areaDescriptionId = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        return model;
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateAreaDescriptionId(model.areaDescriptionId);
            itemView.onUpdateName(model.name);
        }
    }

    private final class EventModel {

        DataListEvent.Type type;

        String previousId;

        ItemModel item;
    }

    private final class ItemModel implements DataList.Item {

        String areaDescriptionId;

        String name;

        @Override
        public String getId() {
            return areaDescriptionId;
        }
    }
}
