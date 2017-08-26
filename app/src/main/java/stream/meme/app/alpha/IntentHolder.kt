package stream.meme.app.alpha

interface
IntentHolder<View> {
    fun attach(attachment: View)
    fun detach()
}