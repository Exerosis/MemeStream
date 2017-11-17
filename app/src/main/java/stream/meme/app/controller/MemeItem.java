package stream.meme.app.controller;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import stream.meme.app.application.Post;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.util.bivsc.ItemBIVSCModule;


public class MemeItem extends ItemBIVSCModule<MemeViewBinding, Post> {
    @Override
    public BiConsumer<Observable<MemeViewBinding>, Observable<Post>> getBinder() {
        return (views, posts) -> {
            views.subscribe(view -> {

            });
        };
    }

    class Intents {
        Observable<>

    }
}
