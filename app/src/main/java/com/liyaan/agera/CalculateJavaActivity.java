package com.liyaan.agera;

import android.os.Bundle;
import android.util.Pair;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.agera.Function;
import com.google.android.agera.Merger;
import com.google.android.agera.MutableRepository;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.RepositoryConfig;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;
import com.liyaan.mynew.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalculateJavaActivity extends AppCompatActivity {
    @BindView(R.id.tvNum1)
    TextView tvNum1;  //数值1
    @BindView(R.id.tvNum2)
    TextView tvNum2;  //数值2
    @BindView(R.id.tvResult)
    TextView tvResult; //计算结果
    @BindView(R.id.tvNumTip1)
    TextView tvNumTip1; //SeekBar上的数值提示
    @BindView(R.id.tvNumTip2)
    TextView tvNumTip2; //SeekBar上的数值提示
    @BindView(R.id.tvOperator)
    TextView tvOperator; //操作符
    @BindView(R.id.rgOperator)
    RadioGroup rgOperator;
    @BindView(R.id.sb1)
    SeekBar sb1;
    @BindView(R.id.sb2)
    SeekBar sb2;
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    MutableRepository<Integer> mValue1Repo = Repositories.mutableRepository(0);
    MutableRepository<Integer> mValue2Repo = Repositories.mutableRepository(0);
    MutableRepository<String> mOperatorRepo = Repositories.mutableRepository("+");
    MutableRepository<String> mResultRepo = Repositories.mutableRepository("0");

    Repository<Result<Integer>> mTaskRepo =
            Repositories.repositoryWithInitialValue(Result.<Integer>absent())
            .observe(mValue1Repo,mValue2Repo,mOperatorRepo)
            .onUpdatesPerLoop()
            .goTo(mExecutor)
            .getFrom(new Supplier<Integer>() {
                @NonNull
                @Override
                public Integer get() {
                    return mValue1Repo.get();
                }
            })
            .mergeIn(new Supplier<Integer>() {
                @NonNull
                @Override
                public Integer get() {
                    return mValue2Repo.get();
                }
            }, new Merger<Integer,  Integer, Pair<Integer, Integer>>() {
                @NonNull
                @Override
                public Pair<Integer, Integer> merge(@NonNull Integer integer, @NonNull Integer tAdd) {
                    return new Pair<>(integer, tAdd);
                }
            })
            .thenTransform(new Function<Pair<Integer, Integer>, Result<Integer>>() {
                @NonNull
                @Override
                public Result<Integer> apply(@NonNull Pair<Integer, Integer> input) {
                    int result = 0;

                    switch (mOperatorRepo.get()) {
                        case "+":
                            result = input.first + input.second;
                            break;

                        case "-":
                            result = input.first - input.second;
                            break;

                        case "*":
                            result = input.first * input.second;
                            break;

                        case "/":   //9. 判断除数是否为0
                            if (input.second != 0) {
                                result = input.first / input.second;
                            } else {
                                return Result.failure(new Throwable("除数不能为0")); //返回失败的Result
                            }
                            break;
                    }
                    return Result.success(result);
                }
            }).onConcurrentUpdate(RepositoryConfig.CANCEL_FLOW).compile();

    //数值1的 Updatable
    Updatable mValue1Updatable = () -> {
        tvNum1.setText(mValue1Repo.get() + "");
        tvNumTip1.setText(mValue1Repo.get() + "");
    };

    //数值2的 Updatable
    Updatable mValue2Updatable = () -> {
        tvNum2.setText(mValue2Repo.get() + "");
        tvNumTip2.setText(mValue2Repo.get() + "");
    };

    //操作符的 Updatable
    Updatable mOperatorUpdatable = () -> {
        tvOperator.setText(mOperatorRepo.get());
    };

    //计算结果的 Updatable
    Updatable mResultUpdatable = () -> {
        tvResult.setText(mResultRepo.get() + "");
    };

    //计算任务的 Updatable
    Updatable mTaskUpdatable = () -> {

        mTaskRepo.get()//11，get方法得到的是一个Result对象
                .ifSucceededSendTo(new Receiver<Integer>() { //当Result成功时
                    @Override
                    public void accept(@NonNull Integer value) {
                        mResultRepo.accept(value + "");
                    }
                })
                .ifFailedSendTo(new Receiver<Throwable>() {//当Result失败时
                    @Override
                    public void accept(@NonNull Throwable value) {
                        mResultRepo.accept(value.getMessage());
                    }
                });
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        ButterKnife.bind(this);
        rgOperator.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.rbAdd:
                    mOperatorRepo.accept("+");  //更新操作符仓库中的值
                    break;
                case R.id.rbSub:
                    mOperatorRepo.accept("-");
                    break;
                case R.id.rbMult:
                    mOperatorRepo.accept("*");
                    break;
                case R.id.rbDiv:
                    mOperatorRepo.accept("/");
                    break;
            }

        });

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mValue1Repo.accept(progress); //更新数值1符仓库中的值
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mValue2Repo.accept(progress); //更新数值2仓库中的值
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册 Updatable
        mValue1Repo.addUpdatable(mValue1Updatable);
        mValue2Repo.addUpdatable(mValue2Updatable);
        mOperatorRepo.addUpdatable(mOperatorUpdatable);
        mResultRepo.addUpdatable(mResultUpdatable);
        mTaskRepo.addUpdatable(mTaskUpdatable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //移除 Updatable
        mValue1Repo.removeUpdatable(mValue1Updatable);
        mValue2Repo.removeUpdatable(mValue2Updatable);
        mOperatorRepo.removeUpdatable(mOperatorUpdatable);
        mResultRepo.removeUpdatable(mResultUpdatable);
        mTaskRepo.removeUpdatable(mTaskUpdatable);
    }
}
