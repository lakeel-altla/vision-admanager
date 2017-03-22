package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.helper.ObservableHelper;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionByAreaListView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.helper.ObservableListEvent;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class UserAreaDescriptionByAreaListPresenter extends BasePresenter<UserAreaDescriptionByAreaListView>
        implements DataList.OnItemListener {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    VisionService visionService;

    @Inject
    Resources resources;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final DataList<Item> items = new DataList<>(this);

    private String areaId;

    @Inject
    public UserAreaDescriptionByAreaListPresenter() {
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

        Disposable disposable1 = ObservableHelper
                .usingList(() -> visionService.getUserAreaDescriptionApi().observeAreaDescriptionsByAreaId(areaId))
                .map(Event::new)
                .subscribe(event -> {
                    items.change(event.type, event.item, event.previousId);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable1);

        Disposable disposable2 = Maybe.<Area>create(e -> {
            visionService.getUserAreaApi().findAreaById(areaId, area -> {
                if (area == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(area);
                }
            }, e::onError);
        }).subscribe(area -> {
            String titleFormat = resources.getString(R.string.title_format_user_area_description_list_in_area);
            String title = String.format(titleFormat, area.getName());
            getView().onUpdateTitle(title);
        }, e -> {
            getLog().e("Failed.", e);
            getView().onSnackbar(R.string.snackbar_failed);
        }, () -> {
            getLog().e("Entity not found.");
            getView().onSnackbar(R.string.snackbar_failed);
        });
        compositeDisposable.add(disposable2);
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        compositeDisposable.clear();
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
        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        Item item = items.get(position);
        getView().onItemSelected(item.areaDescription.getId());
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateAreaDescriptionId(item.areaDescription.getId());
            itemView.onUpdateName(item.areaDescription.getName());
        }
    }

    private final class Event {

        final ObservableListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull ObservableListEvent<AreaDescription> event) {
            type = event.getType();
            item = new Item(event.getData());
            previousId = event.getPreviousChildName();
        }
    }

    private final class Item implements DataList.Item {

        final AreaDescription areaDescription;

        Item(@NonNull AreaDescription areaDescription) {
            this.areaDescription = areaDescription;
        }

        @Override
        public String getId() {
            return areaDescription.getId();
        }
    }
}
