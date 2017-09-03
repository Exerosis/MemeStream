package stream.meme.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor.attachRouter
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction.with


class MainActivity : AppCompatActivity() {
    lateinit var router: Router

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        setContentView(R.layout.main_activity)
        router = attachRouter(this, findViewById(R.id.container), inState)
        if (!router.hasRootController())
            router.setRoot(with(StreamContainerController()))
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}