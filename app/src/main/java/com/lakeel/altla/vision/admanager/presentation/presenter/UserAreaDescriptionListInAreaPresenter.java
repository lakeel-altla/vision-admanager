package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListInAreaView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserAreaDescriptionsByAreaIdUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

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

    private final DataList<Item> items = new DataList<>(this);

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
                .map(Event::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    items.change(event.type, event.item, event.previousId);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable1);

        Disposable disposable2 = findUserAreaUseCase
                .execute(areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(area -> {
                    String titleFormat = resources.getString(R.string.title_format_user_area_description_list_in_area);
                    String title = String.format(titleFormat, area.getName());
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

        final DataListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull DataListEvent<AreaDescription> event) {
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
