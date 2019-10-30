---
author: Jens Buysse, Harm De Weirdt
title: Lesson 6: App Architecture (Persistence)
date: September, 2019
---

# Room

## Overview

![room_overview](C:\Users\FAITH\AndroidStudioProjects\Lesson-6-App-Architecture-Persistence\slides\assets\room_overview.png)

## In depth

![room_indepth](C:\Users\FAITH\AndroidStudioProjects\Lesson-6-App-Architecture-Persistence\slides\assets\room_indepth.png)

# Coroutines

## Callback Hell

### Avoiding callback hell (Java example)

```java
List<Observable<Tile>> observables = new ArrayList<>();
for (int i = 0; i < tileInfoList.size(); i++) {
    observables.add(Observable.create(new Observable.OnSubscribe<Tile>() {
        @Override
        public void call(Subscriber<? super Tile> subscriber) {
            Request r = getRequest(tileInfoList.get(i));
            client.newCall(r).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    subscriber.onNext(getTile(null));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    subscriber.onNext(getTile(response));
                   }
            });
        }
    }));
}
```

### Avoiding callback hell (Javascript example)

```javascript
const makeBurger = nextStep => {
  getBeef(function (beef) {
    cookBeef(beef, function (cookedBeef) {
      getBuns(function (buns) {
        putBeefBetweenBuns(buns, beef, function(burger) {
          nextStep(burger)
        })
      })
    })
  })
}

// Make and serve the burger
makeBurger(function (burger) => {
  serve(burger)
})
```

### What would be better( Javascript example)

Sequential programming 

```javascript
const makeBurger = () => {
  return getBeef()
    .then(beef => cookBeef(beef))
    .then(cookedBeef => getBuns(beef))
    .then(bunsAndBeef => putBeefBetweenBuns(bunsAndBeef));
};

// Make and serve burger
makeBurger().then(burger => serve(burger));
```

## Coroutines

### Coroutines in Android & Kotlin

[![IMAGE ALT TEXT](http://img.youtube.com/vi/ne6CD1ZhAI0/0.jpg)](https://www.youtube.com/watch?v=ne6CD1ZhAI0 "Kotlin Coroutines Introduction")

### Elements of working with coroutines

* Coroutine: a function that can be started and return a result, **but also be suspended and resumed**.
* Coroutine Context: a set of elements describing the context in which a coroutine will be run. Most important are the
  * Coroutine Job: an object that tracks a collection of Coroutines (both started and suspended)
  * Coroutine Dispatcher: determines on what thread the coroutine will be run (Main, IO, Default)
* Coroutine Scope: a wrapper around a Coroutine Context
  * Can cancel coroutines, but can't start them
* Coroutine builder: a function that allows you to start a new coroutine tied to a certain scope

### Testing Coroutines - runBlockingTest

Run all code inside the block in a blocking manner

[Kotlin Documentation]( https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-blocking-test.html )

### More info

[Coroutines on Android (3 parts)](https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb)

## LiveData

### Issues when testing LiveData

* LiveData only emits values when being observed -> you need an observer to get the value
* Can be updated async, so need blocking mechanism
### Solution: LiveData extension functions

Both are ways to temporarily observe a LiveData value and block until you get a result. 

```kotlin
fun <T> LiveData<T>.observeForTesting(
        block: () -> Unit) {
    val observer = Observer<T> { Unit }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}
```

```kotlin
fun <T> LiveData<T>.getValueForTest(): T? {
    var value: T? = null
    val observer = Observer<T> {
        value = it
    }
    observeForever(observer)
    removeObserver(observer)
    return value
}
```

## Exercise

* Refactor the exercise of lesson 5 (the Guess It game) to read the words from a Room database.
* Add an input field to the Title Fragment that allows users to input new words that can also be shown during the game.