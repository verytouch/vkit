package org.example.hystrix.web.command;

import org.example.hystrix.api.ResultUtil;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Component
public class CommandService {

    // 消息订阅封装
    private static final Function<Observable<ResultUtil>, ResultUtil> OBSERVABLE_FUN = observable -> {
        ResultUtil res = ResultUtil.create();
        observable.subscribe(new Observer<ResultUtil>() {
            private int i = 0;
            @Override
            public void onCompleted() {
                if (!res.isFail()) {
                    res.setOk();
                }
            }

            @Override
            public void onError(Throwable e) {
                res.setFail().msg(e.getMessage());
            }

            /*
             * 定义观察者(订阅者)消费信息，可能是多条
             * 此处汇总为ResultUtil
             */
            @Override
            public void onNext(ResultUtil resultUtil) {
                if (resultUtil.isFail()) {
                    res.setFail();
                }
                res.put(++i, resultUtil);
            }
        });
        return res;
    };

    /*
     * 同步，其实是queue().get()
     */
    public ResultUtil execute() {
        return new MyCommand().execute();
    }

    /*
     * 从线程池中取出线程异步执行
     * 但是下面Future.get()是同步的
     * 所以此处等同于execute
     */
    public ResultUtil queue() {
        try {
            return new MyCommand().queue().get();
        } catch (InterruptedException | ExecutionException e) {
            return ResultUtil.fail().msg(e.getMessage());
        }
    }

    /*
     * HystrixObservableCommand以主线程同步调用
     * HystrixCommand也有此方法，但只能发射一次，以线程池调用
     * 调用observe()时马上执行construct()方法
     */
    public ResultUtil observe() {
        // hot Observable
        Observable<ResultUtil> observable = new MyObservableCommand().observe();
        return OBSERVABLE_FUN.apply(observable);
    }

    /*
     * HystrixObservableCommand以主线程同步调用
     * HystrixCommand也有此方法，但只能发射一次，以线程池调用
     * 调用toObserve()后不会马上执行construct()方法，订阅者订阅后才会执行
     */
    public ResultUtil toObserve() {
        // cold Observable
        Observable<ResultUtil> observable = new MyObservableCommand().toObservable();
        return OBSERVABLE_FUN.apply(observable);
    }
}
