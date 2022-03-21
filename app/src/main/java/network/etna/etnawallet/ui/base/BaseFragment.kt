package network.etna.etnawallet.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import network.etna.etnawallet.R
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorService
import network.etna.etnawallet.repository.loadingIndicatorService.LoadingIndicatorState
import org.koin.android.ext.android.inject
import java.util.*

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB: ViewBinding>(
    private val inflate: Inflate<VB>,
    private val titleResourceId: Int? = null,
    private val menuResourceId: Int? = null
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    val safeBinding: VB? get() = _binding

    private val loadingIndicatorService: LoadingIndicatorService by inject()

    open val isLoadingViewHolder: Boolean = false

    private val loadingViewHolderId: String = UUID.randomUUID().toString()

    private val logoLoadingView: View? by lazy {
        view?.findViewById(R.id.logoLoadingView)
    }

    private val loadingViewHolderFragment: BaseFragment<*>? by lazy {
        var f: Fragment? = this
        while (f != null) {

            if (f is BaseFragment<*> && f.isLoadingViewHolder) {
                break
            }
            f = if (f.parentFragment !== f) {
                f.parentFragment
            } else {
                null
            }
        }
        f as? BaseFragment<*>
    }

    protected open val isSecure: Boolean = false

    open fun getTitleObservable(): Observable<String> {
        return Observable.never()
    }

    open fun getLoadingIndicatorStateObservable(): Observable<LoadingIndicatorState> {
        return Observable.never()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLoadingIndicatorStateObservable()
            .distinctUntilChanged()
            .subscribe { state ->
                Log.d("LoadingState", "update $loadingViewHolderId $state")
                loadingViewHolderFragment?.let {
                    Log.d("LoadingState", "update ${it.loadingViewHolderId} $state")
                    loadingIndicatorService.updateState(it.loadingViewHolderId, state)
                }
            }
            .disposedWith(viewLifecycleOwner)

        try {
            loadingIndicatorService
                .getLoadingIndicatorStateObservable(loadingViewHolderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LoadingState", "$loadingViewHolderId $it")
                    logoLoadingView?.visibility = when (it) {
                        LoadingIndicatorState.NORMAL ->  View.INVISIBLE
                        LoadingIndicatorState.LOADING -> View.VISIBLE
                    }
                }
                .disposedWith(viewLifecycleOwner)
        } catch (e: Exception) {}

        try {
            titleResourceId?.let {
                view.findViewById<TextView>(R.id.toolbar_title).text = resources.getString(it)
            }

            getTitleObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.findViewById<TextView>(R.id.toolbar_title).text = it
                }.disposedWith(viewLifecycleOwner)

            menuResourceId?.let {
                val toolbar = view.findViewById<Toolbar>(R.id.includeToolbar)
                toolbar.setOnMenuItemClickListener(this::menuItemSelected)
                view.findViewById<Toolbar>(R.id.includeToolbar).inflateMenu(it)
            }
        } catch (e: Exception) {
        }
        try {
            val back = view.findViewById<ImageView>(R.id.backAction)
            val backStackCount = parentFragmentManager.backStackEntryCount

            val allowBackAction = (activity as? BaseActivity)?.allowBackAction ?: true

            if (allowBackAction && (backStackCount > 0 || !requireActivity().isTaskRoot)) {
                back.setOnClickListener {
                    activity?.onBackPressed()
                }
                back.visibility = View.VISIBLE
            } else {
                back.visibility = View.INVISIBLE
            }

        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSecure) {
            activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? BaseActivity)?.setSwipeRefreshEnabled(true)
        if (isSecure) {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    open fun menuItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}