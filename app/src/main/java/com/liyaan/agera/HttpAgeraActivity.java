package com.liyaan.agera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.android.agera.Function;
import com.google.android.agera.Merger;
import com.google.android.agera.MutableRepository;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.RepositoryConfig;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.liyaan.agera.http.Api;
import com.liyaan.agera.http.Http;
import com.liyaan.mynew.R;



import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

public class HttpAgeraActivity extends AppCompatActivity {
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.tvState)
    TextView tvState;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvProjectTree)
    TextView tvProjectTree;
    @BindView(R.id.tvProjectList)
    TextView tvProjectList;
    //1. 定义一个 MutableRepository ,当点击按钮时，改变其值，监听它的 Repository 就会重新执行
    MutableRepository<Long> mutableRepository =
            Repositories.mutableRepository(System.currentTimeMillis());
    Repository<Result<JSONObject>> repository =
            Repositories.repositoryWithInitialValue(Result.<JSONObject>absent())
            .observe(mutableRepository)
            .onUpdatesPerLoop()
            .goTo(Http.THREAD_POOL)
            .attemptGetFrom(Http.createService(Api.class).getAgeraPic(Http.IMAGE_URL))
            .orEnd(new Function<Throwable, Result<JSONObject>>() {
                @NonNull
                @Override
                public Result<JSONObject> apply(@NonNull Throwable input) {
                    return Result.failure(input);
                }
            }).sendTo(new Receiver<ResponseBody>() {
                @Override
                public void accept(@NonNull ResponseBody value) {
                    try {
                        Thread.sleep(1500);  //模拟网络延迟，便于观察
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //8. 压缩图片
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(),
                            new Rect(0,0,0,0),options);
                    runOnUiThread(() -> { //9. 在主线程中更新UI
                        ivImage.setImageBitmap(bitmap);
                        tvState.setText("正在请求项目列表...");
                    });
                }
            }).attemptGetFrom(Http.createService(Api.class).getProjectTree())
            .orEnd(new Function<Throwable, Result<JSONObject>>() {
                @NonNull
                @Override
                public Result<JSONObject> apply(@NonNull Throwable input) {
                    return Result.failure();
                }
            }).sendTo(new Receiver<JSONObject>() {
                @Override
                public void accept(@NonNull JSONObject value) {
                    try {
                        Thread.sleep(1500);  //模拟网络延迟，便于观察
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {  //13. 在主线程中更新UI
                        tvProjectTree.setText(value.toString());
                        tvState.setText("正在请求项目详情...");
                    });
                }
            }).attemptTransform(new Function<JSONObject, Result<JSONObject>>() {
                @NonNull
                @Override
                public Result<JSONObject> apply(@NonNull JSONObject input) {
                    try {
                        if (input.getInteger("errorCode")==0) {
                            JSONArray objects = input.getJSONArray("data");
                            JSONObject object0 = objects.getJSONObject(0);

                            // 14. 接口获取成功，拿到其中的id字段，进行下一个接口请求
                            return Http.createService(Api.class).getProjectList(object0.getInteger("id")).get();
                        } else {
                            //15. 接口获取数据失败
                            return Result.failure(new Throwable("获取项目分类列表失败!" + input.getString("errorCode")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return Result.failure(new Throwable("获取项目分类列表失败!"));
                }
            }).orEnd(new Function<Throwable, Result<JSONObject>>() {
                @NonNull
                @Override
                public Result<JSONObject> apply(@NonNull Throwable input) {
                    return Result.failure();
                }
            }).thenTransform(new Function<JSONObject, Result<JSONObject>>() {
                @NonNull
                @Override
                public Result<JSONObject> apply(@NonNull JSONObject input) {
                    try {
                        if (input.getInteger("errorCode")==0) {
                            JSONArray objects = input.getJSONObject("data")
                                    .getJSONArray("datas");
                            JSONObject object0 = objects.getJSONObject(0);
                            return Result.success(object0); //18. 请求成功，返回项目详情
                        } else {
                            //19. 请求失败的处理
                            return Result.failure(new Throwable("获取项目详情失败!" + input.getString("errorCode")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //19. 请求失败的处理
                    return Result.failure(new Throwable("获取项目详情失败!"));
                }
            }).onConcurrentUpdate(RepositoryConfig.CANCEL_FLOW)
            .notifyIf(new Merger<Result<JSONObject>, Result<JSONObject>, Boolean>() {
                @NonNull
                @Override
                public Boolean merge(@NonNull Result<JSONObject> jsonObjectResult, @NonNull Result<JSONObject> jsonObjectResult2) {
                    return true;
                }
            }).compile();

    Updatable updatable = new Updatable() {
        @Override
        public void update() {
            repository2.get()
                    .ifSucceededSendTo(new Receiver<JSONObject>() {//22.收到成功的结果，更新UI
                        @Override
                        public void accept(@NonNull JSONObject value) {

                            try {
                                Thread.sleep(1000);  //模拟网络延迟，便于观察
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            tvState.setText("任务执行完毕");
                            tvProjectList.setText(value.toString());
                        }
                    })
                    .ifFailedSendTo(new Receiver<Throwable>() {//23，收到失败的结果，更新UI
                        @Override
                        public void accept(@NonNull Throwable value) {
                            tvState.setText(value.getMessage());
                        }
                    });
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        ButterKnife.bind(this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mutableRepository.accept(System.currentTimeMillis()); //24，当我们点击按钮时，改变mutableRepository中的值

                tvState.setText("正在下载图片...");
                ivImage.setImageBitmap(null);
                tvProjectTree.setText("");
                tvProjectList.setText("");
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        repository2.addUpdatable(updatable); //25.注册 updatable
        tvState.setText("正在下载图片...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        repository2.removeUpdatable(updatable); //26. 注销updatable
    }
    Repository<Result<JSONObject>> repository2 = Repositories
            .repositoryWithInitialValue(Result.<JSONObject>absent())
            .observe(mutableRepository)
            .onUpdatesPerLoop().goTo(Http.THREAD_POOL)
            .attemptGetFrom(Http.createService(Api.class).getAgeraPic(Http.IMAGE_URL))
            .orEnd(Result::failure)
            .sendTo(value->{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(),
                        new Rect(0,0,0,0),options);
                runOnUiThread(()->{
                    ivImage.setImageBitmap(bitmap);
                    tvState.setText("正在加载项目列表");
                });
            }).attemptGetFrom(Http.createService(Api.class).getProjectTree())
            .orEnd(Result::failure)
            .sendTo(value->{
                runOnUiThread(() -> {
                    tvProjectTree.setText(value.toJSONString());
                    tvState.setText("正在请求项目详情...");
                });
            })
            .attemptTransform(input->{
                if (input.getInteger("errorCode")==0){
                    JSONArray objects = input.getJSONArray("data");
                    JSONObject object0 = objects.getJSONObject(0);
                    return Http.createService(Api.class)
                            .getProjectList(object0.getInteger("id")).get();
                }else {
                    return Result.failure(new Throwable("获取项目分类列表失败!" + input.getString("errorCode")));
                }
            }).orEnd(Result::failure)
            .thenTransform(input ->{
                if (input.getInteger("errorCode")==0){
                    JSONArray objects = input.getJSONObject("data").getJSONArray("datas");
                    JSONObject object0 = objects.getJSONObject(0);
                    return Result.success(object0);
                } else {
                    return Result.failure(new Throwable("获取项目详情失败!" + input.getString("errorCode")));
                }
            })
            .onConcurrentUpdate(RepositoryConfig.CANCEL_FLOW)
            .notifyIf((newValue, oldValue) -> true)
            .compile();
}
