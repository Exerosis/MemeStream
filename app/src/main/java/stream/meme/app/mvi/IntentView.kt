package stream.meme.app.mvi

import io.reactivex.Observable

interface IntentView<ViewState> {
    fun render(state: Observable<ViewState>);
}