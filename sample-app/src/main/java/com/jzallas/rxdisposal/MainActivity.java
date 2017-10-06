package com.jzallas.rxdisposal;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleBinder;
import com.jzallas.rxdisposal.lifecycleaware.SubscriptionAutoDisposal;

import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView textView0, textView1, textView2, textView3;
    private ProgressBar progressBar;

    // manual disposal
    private SubscriptionDisposal disposalAll = new SubscriptionDisposal(new CompositeDisposal());
    private SubscriptionDisposal disposalOne = new SubscriptionDisposal(new SerialDisposal());

    // automatic disposal
    @LifecycleAware(Lifecycle.Event.ON_PAUSE)
    SubscriptionAutoDisposal autoDisposal = new SubscriptionAutoDisposal(new SerialDisposal());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LifecycleBinder.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        textView0 = findViewById(R.id.textview0);
        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);
        textView3 = findViewById(R.id.textview3);

        progressBar = findViewById(R.id.progressBar);

        // start the streams when the button is pressed
        fab.setOnClickListener(view -> {
            fab.setImageResource(android.R.drawable.ic_menu_revert);
            startWork();
        });
    }

    private void startWork() {
        // dispose of any previous streams
        stopWork();

        /*
         *  1) Stream that reports current time -
         *       This stream will always attempt to update the time until the Disposal is triggered
         *       This stream is ultimately registered to CompositeDisposal for handling multiple events
         */
        Observable.interval(1, TimeUnit.SECONDS)
                .map(aLong -> LocalTime.now())
                .map(time -> DateTimeFormat.mediumTime().print(time))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposalAll.wrap(textView1::setText));

        /*
         *  2) Stream that reports a random ID -
         *       This stream will always attempt to post an ID the Disposal is triggered.
         *       This stream is ultimately registered to SimpleDisposal for handling individual events
         */
        Observable.interval(1, TimeUnit.SECONDS)
                .map(aLong -> UUID.randomUUID())
                .map(UUID::toString)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposalOne.wrap(textView2::setText));

        /*
         *  3) Stream that reports time elapsed -
         *       This stream will always attempt to update the time until the Disposal is triggered.
         *       This stream is ultimately registered to CompositeDisposal for handling multiple events.
         */
        Observable.interval(1, TimeUnit.SECONDS)
                .map(Duration::standardSeconds)
                .map(Duration::toPeriod)
                .map(period -> PeriodFormat.getDefault().print(period))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposalAll.wrap(textView3::setText));

        /*
         *  4) Stream that simulates download progress -
         *      This stream will create random progress until it reaches 100 %
         *      This stream is registered to the SubscriptionAutoDisposal and will automatically
         *      dispose during onPause(...)
         */
        Random random = new Random(System.currentTimeMillis());
        Observable.interval(1, TimeUnit.SECONDS)
                .map((t) -> random.nextInt(5))
                .scan((x, y) -> x + y)
                .takeUntil((sum) -> sum > progressBar.getMax())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(autoDisposal.wrap(
                        this::updateProgressBar,
                        this::handleError,
                        () -> updateProgressBar(progressBar.getMax())
                ));
    }

    private void handleError(Throwable e) {
        Log.e(MainActivity.class.getName(), "error", e);
    }

    private void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
        textView0.setText(getString(R.string.progressbar_text, progress, progressBar.getMax()));
    }

    private void stopWork() {
        disposalOne.dispose();
        disposalAll.dispose();
        Toast.makeText(this, R.string.message_disposal, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        stopWork();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
