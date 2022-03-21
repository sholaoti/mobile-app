package network.etna.etnawallet.repository.warningservice

import io.reactivex.Observable
import java.util.*

interface WarningService {
    fun getWarningObservable(): Observable<Optional<String>>
    fun getErrorObservable(): Observable<Optional<String>>
    fun cancelIssue(issueId: String)
    fun handleIssue(throwable: Throwable, permanent: Boolean = false): String
}