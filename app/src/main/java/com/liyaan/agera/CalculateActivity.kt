package com.liyaan.agera

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.agera.*
import com.google.android.agera.Function
import com.google.android.agera.Result.absent
import com.liyaan.mynew.R
import kotlinx.android.synthetic.main.activity_cal.*
import java.util.concurrent.Executors

class CalculateActivity:AppCompatActivity() {
    private val mExecutor = Executors.newSingleThreadExecutor() //计算线程
    private val mValue1Repo =
        Repositories.mutableRepository(0)
    private val mValue2Repo =
        Repositories.mutableRepository(0)
    private val mOperatorRepo =
        Repositories.mutableRepository("+")
    private val mResultRepo =
        Repositories.mutableRepository("0")

//    private val mTaskRepo:Repository<Result<Int>> =
//        Repositories.repositoryWithInitialValue(absent<Int>())
//        .observe(mValue1Repo, mValue2Repo, mOperatorRepo)
//        .onUpdatesPerLoop().goTo(mExecutor)
//        .getFrom { mValue1Repo.get() }
//        .mergeIn(Supplier<Int> {
//                    mValue2Repo.get()
//                },
//                Merger<Int, Int, Pair<Int, Int>> { first, second ->
//                    Pair(first,second)
//                })
//        .thenTransform(FunctionInit(mOperatorRepo))
//            .onConcurrentUpdate(RepositoryConfig.CANCEL_FLOW).compile() as Repository<Result<Int>>
    private val mValue1Updatable = Updatable {
        tvNum1.text = "${mValue1Repo.get()}"
        tvNumTip1.text = "${mValue1Repo.get()}"
    }

    private val mValue2Updatable = Updatable {
        tvNum2.text = "${mValue2Repo.get()}"
        tvNumTip2.text = "${mValue2Repo.get()}"
    }

    private val mOperatorUpdatable = Updatable {
        tvOperator.text = "${mOperatorRepo.get()}"
    }
    private val mResultUpdatable = Updatable {
        tvResult.text = "${mResultRepo.get()}"
    }

    private val mTaskUpdatable = Updatable {
//        mTaskRepo.get().ifSucceededSendTo {
//            mResultRepo.accept("$it")
//        }.ifFailedSendTo {
//            mResultRepo.accept(it.message!!)
//        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal)
        rgOperator.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbAdd->mOperatorRepo.accept("+")
                R.id.rbSub->mOperatorRepo.accept("-")
                R.id.rbMult->mOperatorRepo.accept("*")
                R.id.rbDiv->mOperatorRepo.accept("/")
            }
        }
        sb1.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mValue1Repo.accept(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        sb2.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mValue2Repo.accept(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }
    override fun onResume() {
        super.onResume()
        //注册 Updatable
        mValue1Repo.addUpdatable(mValue1Updatable)
        mValue2Repo.addUpdatable(mValue2Updatable)
        mOperatorRepo.addUpdatable(mOperatorUpdatable)
        mResultRepo.addUpdatable(mResultUpdatable)
//        mTaskRepo.addUpdatable(mTaskUpdatable)
    }

    override fun onPause() {
        super.onPause()
        //移除 Updatable
        mValue1Repo.removeUpdatable(mValue1Updatable)
        mValue2Repo.removeUpdatable(mValue2Updatable)
        mOperatorRepo.removeUpdatable(mOperatorUpdatable)
        mResultRepo.removeUpdatable(mResultUpdatable)
//        mTaskRepo.removeUpdatable(mTaskUpdatable)
    }
}