RxDisposal
============

[![CircleCI](https://circleci.com/gh/jzallas/RxDisposal/tree/master.svg?style=shield)](https://circleci.com/gh/jzallas/RxDisposal/tree/master)

A convenient 'garbage disposal' that can hook into an rx streams.

 * Eliminate `Disposable` maintenance
 * Keep up that beautiful rx chain
 * Remove a little boilerplate

Getting Started
---------------
##### Setup your `Disposal`
Pick a `Disposal` with the `Disposable` storage strategy that fits your needs best. Then, wrap it inside a `SubscriptionDisposal` for easier use in an rx chain:

```java
SubscriptionDisposal myDisposal = new SubscriptionDisposal(new CompositeDisposal());
```

##### Registering a `Disposable`
Now you can plug in your `SubscriptionDisposal` every time you call `subscribe(...)` to automatically capture and remember the last `Disposable` product in that chain.

```java
myObservable.map(a -> doStuff(a))
            .map(s -> doStuff2(s))
            .map(d -> doStuff3(d))
            .subscribe(myDisposal.wrap(f -> print(f)));
```

Disposing Manually
---------------
`SubscriptionDisposal` is still a `Disposal`. You can call `dispose()` on it and it will delegate that request to the `Disposal` strategy you initially provided.

```java
myDisposal.dispose();
```

Disposing Automatically
------------------
If you're using the [LifecycleAware](https://github.com/jzallas/LifecycleAware) library on Android, then you can also create `Disposal`s that automatically dispose for you. The only extra thing you need to do is decide when (what lifecycle hook) you want your `Disposal` to auto dispose.

```java
@LifecycleAware(Lifecycle.Event.ON_PAUSE)
SubscriptionAutoDiposal autoDisposal = new SubscriptionAutoDisposal(myDisposal);

// this should automatically dispose in onPause(...) if you're using LifecycleAware correctly
myObservable.map(a -> doStuff(a))
            .subscribe(autoDisposal.wrap(b -> print(b)));
```

Samples
-------
You can find examples in the included [sample app](/sample-app).

Dependencies
------------
```groovy
dependencies {
    implementation 'com.jzallas:rxdisposal:0.1.0'

    // if you want to use Automatic Disposal
    implementation 'com.jzallas:rxdisposal-lifecycleaware:0.1.0'
}
```

License
-------

    Copyright 2017 Jon Zallas

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.