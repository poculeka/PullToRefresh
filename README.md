# PullToRefresh Composable

This repository contains Jetpack Compose implementation of pull to refresh layout.

Here's what its basic usage looks like:

```kotlin
@Composable
fun composableFun() {
    var isRefreshing: Boolean by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf( /** items **/ ) }

    PullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            // update items and set isRefreshing = false
        }
    ) {
        // Any composable content can go here
        LazyColumn {
            items.map { Text("I'm $it") }
        }
    }
}
```

You can find more in [sample](https://github.com/poculeka/PullToRefresh/blob/master/pulltorefresh/app/src/main/java/com/puculek/pulltorefresh/samples/PullToRefreshSample.kt)

# Dependency

You can include _PullToRefresh_ in your project from `mavenCentral()`:
```groovy
    implementation 'com.puculek.pulltorefresh:pull-to-refresh-compose:1.0.0'
```

# Showcase

![](gifs/pulltorefresh.gif)

Have fun!
