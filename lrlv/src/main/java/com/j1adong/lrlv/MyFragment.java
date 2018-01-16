package com.j1adong.lrlv;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by J1aDong on 2018/1/14.
 */

public class MyFragment extends RxFragment {

    @BindView(R.id.tv_content)
    TextView mTvContent;
    Unbinder unbinder;
    private MyViewModel model;
    private MyDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        model.getMyBean().observe(this, new Observer<MyBean>() {
            @Override
            public void onChanged(@Nullable MyBean myBean) {
                mTvContent.setText(myBean.toString());
            }
        });

        db = MyDatabase.getDatabase(getContext());
        db.userDao().queryAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(MyFragment.this.<MyBean[]>bindToLifecycle())
                .subscribe(new Consumer<MyBean[]>() {
                    @Override
                    public void accept(MyBean[] myBean) throws Exception {
                        LogUtils.w("myBeans-->" + myBean.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
