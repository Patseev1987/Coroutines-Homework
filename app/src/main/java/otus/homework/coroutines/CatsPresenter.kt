package otus.homework.coroutines

import kotlinx.coroutines.*
import java.net.SocketTimeoutException

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName("CatsCoroutine"))

    fun onInitComplete() {
        scope.launch {
            try {
                _catsView?.populate(catsService.getCatFact())
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    _catsView?.showToast(TIME_OUT)
                } else {
                    CrashMonitor.trackWarning()
                    _catsView?.showToast(e.message ?: e.toString())
                }
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
        scope.cancel()
    }

    companion object {
        const val TIME_OUT = "Не удалось получить ответ от сервера"
    }
}