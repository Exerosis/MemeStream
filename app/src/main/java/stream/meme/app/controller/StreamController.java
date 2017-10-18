package stream.meme.app.controller;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableByte;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.Meme;
import stream.meme.app.application.MemeStream;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.databinding.StreamViewBinding;
import stream.meme.app.util.ItemOffsetDecoration;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.rxadapter.RxAdapter;
import stream.meme.app.util.rxadapter.RxListCallback;
import stream.meme.app.util.rxadapter.RxPagination;

import static android.support.v4.util.Pair.create;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.visibility;

public class StreamController extends DatabindingBIVSCModule<StreamViewBinding, StreamController.State> {
    private final Intents intents = new Intents();
    private MemeStream memeStream;
    private int page = 1;

    public StreamController() {
        super(R.layout.stream_view);
    }

    @Override
    public BiConsumer<Observable<StreamViewBinding>, Observable<State>> getBinder() {
        return (views, state) -> {
            intents.RefreshIntent = views.switchMap(view -> refreshes(view.refreshLayout));

            new RxAdapter<>(views.map(view -> view.recyclerView),
                    new RxListCallback<>(state.map(State::memes)))
                    .bind(R.layout.meme_view, (Meme meme, MemeViewBinding memeView) -> {
                        //Add a shown observable if there isn't already one.
                        if (memeView.getShown() == null)
                            memeView.setShown(new ObservableBoolean(false));
                        if (memeView.getRating() == null)
                            memeView.setRating(new ObservableByte((byte) 0));

                        List<Comment> comments = new ArrayList<>();
                        comments.add(new Comment("Exerosis", "10/17/2017", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet odio felis. Sed et bibendum mauris. Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultricies libero. Donec sed scelerisque lectus. Aenean viverra hendrerit laoreet. Vivamus ullamcorper risus ut nibh sodales accumsan. Cras est diam, mattis in pulvinar ac, imperdiet sit amet elit. Ut a erat viverra, gravida velit at, rutrum nulla. In fermentum nulla vel urna bibendum malesuada. "));
                        comments.add(new Comment("Exerosis", "10/17/2017", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet odio felis. Sed et bibendum mauris. Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultricies libero. Donec sed scelerisque lectus. Aenean viverra hendrerit laoreet. Vivamus ullamcorper risus ut nibh sodales accumsan. Cras est diam, mattis in pulvinar ac, imperdiet sit amet elit. Ut a erat viverra, gravida velit at, rutrum nulla. In fermentum nulla vel urna bibendum malesuada. "));
                        comments.add(new Comment("Exerosis", "10/17/2017", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet odio felis. Sed et bibendum mauris. Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultricies libero. Donec sed scelerisque lectus. Aenean viverra hendrerit laoreet. Vivamus ullamcorper risus ut nibh sodales accumsan. Cras est diam, mattis in pulvinar ac, imperdiet sit amet elit. Ut a erat viverra, gravida velit at, rutrum nulla. In fermentum nulla vel urna bibendum malesuada. "));
                        memeView.setComments(comments);

                        //Add meme information.
                        Picasso.with(getActivity()).load(meme.getImage()).into(memeView.image);
                        memeView.title.setText(meme.getTitle());
                        memeView.subtitle.setText(meme.getSubtitle());

                        //Setup ratings intents
                        clicks(memeView.like).map(ignored -> 1).mergeWith(clicks(memeView.dislike).map(ignored -> -1)).doOnNext(rating ->
                                memeView.getRating().set(rating.byteValue())).subscribe(rating ->
                                intents.RatedIntent.onNext(create(meme, rating.byteValue())));

                        //Bind expanding layout to toggle.
                        clicks(memeView.toggle).subscribe(ignored ->
                                memeView.expandableLayout.setExpanded(memeView.toggle.isChecked()));

                        //Add view click listener.
                        memeView.getRoot().setOnClickListener(v -> intents.MemeClickIntent.onNext(meme));
                    }).footer(R.layout.stream_footer).showFooter(state.map(State::nextPageLoading).distinctUntilChanged());

            views.subscribe(view -> {
                view.recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.item_offset));
                view.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                RxPagination.on(view.recyclerView, state.map(State::nextPageLoading).distinctUntilChanged()).subscribe(intents.LoadNextIntent);
                state.map(State::firstPageLoading).distinctUntilChanged().subscribe(visibility(view.progressBar));
                state.map(State::refreshing).distinctUntilChanged().subscribe(refreshing(view.refreshLayout));
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Observable.merge(
                intents.LoadNextIntent
                        .flatMap(ignored -> memeStream.loadMemes(page++)
                                .map(Partial::NextPageLoaded)
                                .startWith(Partial.NextPageLoading())
                                .onErrorReturn(Partial::NextPageError)),
                intents.LoadFirstIntent
                        .flatMap(ignored -> memeStream.loadMemes(0)
                                .map(Partial::FirstPageLoaded)
                                .startWith(Partial.FirstPageLoading())
                                .onErrorReturn(Partial::FirstPageError)),
                intents.RefreshIntent
                        .flatMap(ignored -> memeStream.loadMemes(page++)
                                .map(Partial::Refreshed)
                                .startWith(Partial.Refreshing())
                                .onErrorReturn(Partial::RefreshError)))
                .scan(new State(), (state, partial) -> partial.apply(state));
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        Fresco.initialize(context);
        super.onContextAvailable(context);
    }

    class Intents {
        Observable<Object> RefreshIntent;
        Observable<Boolean> LoadFirstIntent = Observable.just(true);
        Subject<Boolean> LoadNextIntent = PublishSubject.create();
        Subject<Meme> MemeClickIntent = PublishSubject.create();
        Subject<Pair<Meme, Byte>> RatedIntent = PublishSubject.create();
        Subject<Meme> ShareClickIntent = PublishSubject.create();
    }

    class State {
        boolean refreshing = false;
        boolean nextPageLoading = false;
        boolean firstPageLoading = false;
        Throwable error = null;
        LinkedList<Meme> memes = new LinkedList<>();

        public boolean refreshing() {
            return refreshing;
        }

        public boolean nextPageLoading() {
            return nextPageLoading;
        }

        public boolean firstPageLoading() {
            return firstPageLoading;
        }

        public Throwable error() {
            return error;
        }

        public LinkedList<Meme> memes() {
            return memes;
        }
    }

    interface Partial {
        static Function<State, State> NextPageLoading() {
            return state -> {
                state.nextPageLoading = true;
                return state;
            };
        }

        static Function<State, State> NextPageError(Throwable error) {
            return state -> {
                state.nextPageLoading = false;
                state.error = error;
                return state;
            };
        }

        static Function<State, State> NextPageLoaded(List<Meme> memes) {
            return state -> {
                state.nextPageLoading = false;
                for (Meme meme : memes)
                    state.memes.addLast(meme);
                return state;
            };
        }

        static Function<State, State> FirstPageLoading() {
            return state -> {
                state.firstPageLoading = true;
                return state;
            };
        }

        static Function<State, State> FirstPageError(Throwable error) {
            return state -> {
                state.firstPageLoading = false;
                state.error = error;
                return state;
            };
        }

        static Function<State, State> FirstPageLoaded(List<Meme> memes) {
            return state -> {
                state.firstPageLoading = false;
                state.memes.addAll(memes);
                return state;
            };
        }

        static Function<State, State> Refreshing() {
            return state -> {
                state.refreshing = true;
                return state;
            };
        }

        static Function<State, State> RefreshError(Throwable error) {
            return state -> {
                state.refreshing = false;
                state.error = error;
                return state;
            };
        }

        static Function<State, State> Refreshed(List<Meme> memes) {
            return state -> {
                state.refreshing = false;
                for (Meme meme : Lists.reverse(memes))
                    if (!state.memes.contains(meme))
                        state.memes.addFirst(meme);
                return state;
            };
        }
    }
}