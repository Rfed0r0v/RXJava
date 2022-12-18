package com.example.rxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(DebugTree())
        setContentView(R.layout.activity_main)

        val someObservable = Observable.just(-1, 0, 1, 1, 3, 4, 5, 6, 7, 10)

        someObservable
            .filter {
                it <= 5
            }
            .repeat(2)
            .distinctUntilChanged()
            .map {
                it.toString()
            }.subscribe(
                {
                    Timber.d("ObserverOut: $it")
                }, {}, {}
            )


//       Подписчики
        val subscriber1 = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Timber.tag("RxJava3").d("onSubscribe ")
            }

            override fun onNext(t: Int) {
                Timber.d("onNext ${t * 10}")
            }

            override fun onError(e: Throwable) {
                Timber.d("onError $e")
            }

            override fun onComplete() {
                Timber.d("onComplete ")
            }
        }
        val subscriber2 = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Timber.d("onSubscribe 2 ")
            }

            override fun onNext(t: Int) {
                val divideByZero = t / 0
                Timber.d("onNext 2 $divideByZero")
            }

            override fun onError(e: Throwable) {
                Timber.d("onError 2$e")
            }

            override fun onComplete() {
                Timber.d("onComplete 2 ")
            }
        }

// -----------------------------------------------------------------------
// Переключение потоков
        someObservable
            .subscribeOn(Schedulers.newThread())
            .doOnNext {
                Timber.d("doOnNext ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Timber.d("doOnNext ${Thread.currentThread().name}")
            }
            .subscribe(
                {
                    Timber.d("onNext $it")
                },
                {},
                {}
            )

//------------------------------------------------------------------------------------
        Flowable
            .just(0, 0, 2, 4, 5, 6, 7, 8, 9, 10)
            .onBackpressureBuffer(4)
            .subscribe({
                Timber.d(
                    "onNext $it"
                )
            }, {}, {}
            )
// -----------------------------------------------------------------------------------
        Single.just(1)
            .subscribe({

            }, { })


    }
}