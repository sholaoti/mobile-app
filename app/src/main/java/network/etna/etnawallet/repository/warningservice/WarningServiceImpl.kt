package network.etna.etnawallet.repository.warningservice

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import network.etna.etnawallet.R
import network.etna.etnawallet.sdk.error.BaseError
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class WarningServiceImpl(val context: Context): WarningService {

    private val issuesSubject: BehaviorSubject<List<IssueItem>> =
        BehaviorSubject.createDefault(emptyList())

    init {
        Observable
            .interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe {

                issuesSubject.value?.let {
                    val actualWarnings = it.filter { it.expiryDate == null || it.expiryDate > Date() }
                    if (it.size != actualWarnings.size) {
                        issuesSubject.onNext(actualWarnings)
                    }
                }

            }
    }

    private fun getIssueObservable(type: IssueItemType): Observable<Optional<String>> {
        return issuesSubject
            .map { warnings ->
                Optional.ofNullable(warnings.firstOrNull { it.type == type })
            }
            .distinctUntilChanged()
            .map {
                if (it.isPresent) {
                    Optional.of(it.get().text)
                } else {
                    Optional.empty()
                }
            }
    }

    override fun getWarningObservable(): Observable<Optional<String>> {
        return getIssueObservable(IssueItemType.WARNING)
    }

    override fun getErrorObservable(): Observable<Optional<String>> {
        return getIssueObservable(IssueItemType.ERROR)
    }


    private fun showIssue(resource: Int, type: IssueItemType, permanent: Boolean): String {
        val text = context.resources.getString(resource)
        val id = UUID.randomUUID().toString()

        issuesSubject.value?.let {
            val issues = it.toMutableList()
            if (permanent) {
                issues.add(IssueItem(id, type, text, null))
            } else {
                val cdate = Calendar.getInstance()
                cdate.time = Date()
                cdate.add(Calendar.SECOND, 3)
                issues.add(IssueItem(id, type, text, cdate.time))
            }

            issuesSubject.onNext(issues)
        }

        return id
    }

    override fun cancelIssue(issueId: String) {
        issuesSubject.value?.let {
            val actualWarnings = it.filter { it.id != issueId }
            if (it.size != actualWarnings.size) {
                issuesSubject.onNext(actualWarnings)
            }
        }
    }

    override fun handleIssue(throwable: Throwable, permanent: Boolean): String {
        return when (throwable) {
            is BaseError.Warning -> showIssue(throwable.resourceId, IssueItemType.WARNING, permanent)
            is BaseError.Error -> showIssue(throwable.resourceId, IssueItemType.ERROR, permanent)
            else -> showIssue(R.string.error_smth_wrong, IssueItemType.WARNING, permanent)
        }
    }
}