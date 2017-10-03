RxDisposal
============

[![CircleCI](https://circleci.com/gh/jzallas/RxDisposal/tree/master.svg?style=shield)](https://circleci.com/gh/jzallas/RxDisposal/tree/master)

A convenient 'garbage disposal' that can hook into an rx streams.

 * Eliminate `Disposable` maintenance
 * Keep up that beautiful rx chain
 * Remove a little boilerplate

```java
void withRxDisposal(Observable<String> data) {
  // registering a disposable
  CompositeDisposal compositeDisposal = new CompositeDisposal();
    data.map(a -> doStuff(a))
        .map(s -> doStuff2(s))
        .map(d -> doStuff3(d))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(disposal.wrap(f -> print(f)));

  // disposing
  compositeDisposal.dispose();
}
```

Automatic Disposal
------------------
coming soon...


Download
--------

```groovy
dependencies {
  // TBD
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