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
                },
                {},
                {}
            )


//       Подписчики
        val subscriber1 = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Timber.tag("RxJava3").d("onSubscribe ")
            }

            override fun onNext(t: Int) {
                Timber.d( "onNext ${t * 10}")
            }

            override fun onError(e: Throwable) {
                Timber.d( "onError $e")
            }

            override fun onComplete() {
                Timber.d( "onComplete ")
            }
        }
        val subscriber2 = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Timber.d( "onSubscribe 2 ")
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

//        Потоки
//        doOnNext вся труба с объектами выполняется в main потоке
//        Thread.currentThread().name- дает название общего потока
//        Как переключить поток в RX:subscribeOn
//        Есть определенные константы Schedulers
//         Константа Schedulers.io()- для работы с input и output
//         Константа Schedulers.newThread()- создает отдельный thread. Если его поместить
//         до doOnNext- создается отдельный поток, в котором вызываются Item
//        Константа AndroidSchedulers.mainThread()- переключение на главный поток до
//         момента пока либо не зхакончится код, либо не встретится observeOn
//         в случае ниже, создался один поток, он идт до того момента, пока не увидил
//         observeOn - и создается новый поток
//                      .subscribeOn(Schedulers.newThread() -
//                    .observeOn(Schedulers.newThread())

        someObservable
            .subscribeOn(Schedulers.newThread())
            .doOnNext {
                Timber.d("doOnNext ${Thread.currentThread().name}")
            }
//            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Timber.d( "doOnNext ${Thread.currentThread().name}")
            }
            .subscribe(
                {
                    Timber.d( "onNext $it")
                },
                {},
                {}
            )

//        Flowable - Observer который может работать с потоком больших данных и может его
//         регулировать с помощью технологии Strategy
//        Методы .onBackpressureBuffer() - позволяет буферизирует до определенного кол-ва элементов
//         позволяет подключить определенную стратегию обработки данных. Если подписчики не успеют
//         обработать данные до того момента когда  Flowable выкинет данные из буфера заданного объема
//        то тогдабудеет вызван OnError, потомучто subscriber не успеет обработать данные
//
//        Мы можем выбирать - буферизировать данные,или отбрасываем самые старые события, можем откидывать данные

        Flowable
            .just(1, 1, 1, 4, 5, 6, 7, 8, 9, 10)
            .onBackpressureBuffer(4)
            .subscribe({
                Timber.d("onNext $it"
                )
            }, {}, {}
            )


        // Single - Observable, который вызывается единожды, получает данные и умирает
        // livedata сетится только на главном потоке
        Single.just(1)
            .subscribe({

            }, {

            })


    }
}
