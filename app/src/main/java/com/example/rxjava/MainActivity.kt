package com.example.rxjava

import android.os.Bundle
import android.util.Log
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

//
//        Отфильтровать, пропустим или не пропустим объект  дальше
//        Оператор Map - можем изменять данные
//        distinctUntilChanged - удаляет дубликаты
//
//        debounce - стопорит все процессы в течение указанного времени и
//         берет последний элемент, который за это время пришел, полезно для ситуации
//         когда пользователь часто кликает на ui, чтобы отсекать uiEvents
//          .debounce(100L, TimeUnit.MILLISECONDS)
//
//        delay отправляет данные с задержкой:
//         отдает данные в трубу спстя 5 секунд, как на него подписались
//        .delay (5000L, TimeUnit.MILLISECONDS)
//
//        .repeat(2) - повторяет событиe заданное кол-во раз

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


//         Как добавляем подписчиков
        val subscriber1 = object : Observer<Int> {
            //Подписаться, вызывается, когда подписчик подписался, и возвращает диспосбл, объект, на который мы подписались
            override fun onSubscribe(d: Disposable) {
                //имеет метод d.dispose() - метод, чтобы всех подписчиков отписать,
                Timber.tag("RxJava3").d("onSubscribe ")
            }

            //Прописывается логика, что мы должны сделать с данными, когда они пришли, try catch прописывать не надо
            //Срабатывает на каждый элемент из Just
            override fun onNext(t: Int) {
                Log.d("RxJava3", "onNext ${t * 10}")
            }

            //Вызывается, когда не сработал OnNext (произошла ошибка), также как и корутины с манадой
            //Используется для отлавливания ошибок, "местный try catch"
            override fun onError(e: Throwable) {
                Log.d("RxJava3", "onError $e")
            }

            //Если нет ошибки в OnNext вызывается onComplete
            override fun onComplete() {
                Log.d("RxJava3", "onComplete ")
            }
        }
        val subscriber2 = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RxJava3", "onSubscribe 2 ")
            }

            override fun onNext(t: Int) {
                val divideByZero = t / 0
                Log.d("RxJava3", "onNext 2 $divideByZero")
            }

            override fun onError(e: Throwable) {
                Log.d("RxJava3", "onError 2$e")
            }

            override fun onComplete() {
                Log.d("RxJava3", "onComplete 2 ")
            }
        }

//        Как этих подписчиков добавляем в Observable
//        someObservable.subscribe(subscriber1)
//        someObservable.subscribe(subscriber2)


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
                Log.d("RxJava3", "doOnNext ${Thread.currentThread().name}")
            }
//            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.d("RxJava3", "doOnNext ${Thread.currentThread().name}")
            }
            .subscribe(
                {
                    Log.d("RxJava3", "onNext $it")
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
                Log.d(
                    "RxJava3", "onNext $it"
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
