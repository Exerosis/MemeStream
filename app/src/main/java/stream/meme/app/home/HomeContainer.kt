package stream.meme.app.home

import io.reactivex.Observable
import stream.meme.app.mvi.IntentView

interface HomeContainer : IntentView<HomeViewState> {
    val CloseDrawerIntent: Observable<Void>
    val NavigateIntent: Observable<Int>
    val OpenProfileIntent: Observable<Void>
}