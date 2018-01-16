package com.j1adong.lrlv;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;

import org.reactivestreams.Publisher;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_submit)
    Button mBtnSubmit;
    @BindView(R.id.tv_show)
    TextView mTvShow;
    private MyViewModel model;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getLifecycle().addObserver(new MyObserver());

        model = ViewModelProviders.of(this).get(MyViewModel.class);

        model.getMyBean().observe(this, new Observer<MyBean>() {
            @Override
            public void onChanged(@Nullable MyBean myBean) {
                mTvShow.setText(myBean.toString());
            }
        });

        db = MyDatabase.getDatabase(this);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mEtName.getText().toString();
                final String password = mEtPassword.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showShort("请填写用户名");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort("请填写密码");
                    return;
                }

                Flowable.defer(new Callable<Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> call() throws Exception {
                        MyBean myBean = db.userDao().queryName(name);
                        if (null != myBean) {
                            return Flowable.just(true);
                        }
                        db.userDao().save(new MyBean(name, password));
                        return Flowable.just(false);
                    }
                })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(MainActivity.this.<Boolean>bindToLifecycle())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isExist) throws Exception {
                                if (!isExist) {
                                    model.setMyBean(new MyBean(name, password));
                                } else {
                                    ToastUtils.showShort("已经被注册");
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
            }
        });
    }
}