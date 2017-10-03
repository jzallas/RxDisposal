package com.jzallas.rxdisposal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView textView1, textView2, textView3;
    private CompositeDisposal disposalAll = new CompositeDisposal();
    private SimpleDisposal disposalOne = new SimpleDisposal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);
        textView3 = findViewById(R.id.textview3);

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
        *       This stream is registered to CompositeDisposal for handling multiple events
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
        *       This stream is registered to SimpleDisposal for handling individual events
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
        *       This stream is registered to CompositeDisposal for handling multiple events.
        */
        Observable.interval(1, TimeUnit.SECONDS)
                .map(Duration::standardSeconds)
                .map(Duration::toPeriod)
                .map(period -> PeriodFormat.getDefault().print(period))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposalAll.wrap(textView3::setText));
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
