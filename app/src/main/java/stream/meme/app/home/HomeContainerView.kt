package stream.meme.app.home

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import com.bluelinelabs.conductor.Conductor.attachRouter
import com.bluelinelabs.conductor.Router
import com.jakewharton.rxbinding2.support.design.widget.RxNavigationView
import com.jakewharton.rxbinding2.support.v4.widget.RxDrawerLayout.open
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import stream.meme.app.R
import stream.meme.app.databinding.HomeContainerViewBinding

class HomeContainerView : AppCompatActivity(), HomeContainer {
    override val CloseDrawerIntent: Observable<Void>
        get() = PublishSubject.create();
    override val NavigateIntent: Observable<Int>
        get() = PublishSubject.create();
    override val OpenProfileIntent: Observable<Void>
        get() = PublishSubject.create();

    lateinit var router: Router
    lateinit var binding: HomeContainerViewBinding

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        binding = setContentView(this, R.layout.home_container_view)
        RxNavigationView.itemSelections(binding.navigationView).
        router = attachRouter(this, binding.container, inState)
    }

    override fun render(state: Observable<HomeViewState>) {
        state.map { it.DrawerOpen }.subscribe(open(binding.drawerLayout, Gravity.START))
        state.map { it.DrawerOpen }.subscribe(open(binding.drawerLayout, Gravity.START))
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}