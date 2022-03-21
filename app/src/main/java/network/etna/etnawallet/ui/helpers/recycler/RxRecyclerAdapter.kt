package network.etna.etnawallet.ui.helpers.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlin.collections.ArrayList

typealias BindView<T> = (View) -> T

class BindModel<VB: ViewBinding, M>(
    val binding: VB,
    val model: M,
    val position: Int,
    val totalCount: Int
)
class OnItemClickModel<M>(
    val model: M,
    val position: Int
)
typealias BindItem<VB, M> = (BindModel<VB, M>) -> Unit
typealias OnItemClick<M> = (OnItemClickModel<M>) -> Unit

class RxRecyclerAdapter<VB: ViewBinding, M>(
    lifecycleOwner: LifecycleOwner,
    private val viewHolderLayoutId: Int,
    private val bindView: BindView<VB>,
    private val dataObservable: Observable<List<M>>,
    private val bindItem: BindItem<VB, M>,
    private val onItemClick: OnItemClick<M>? = null
) : RecyclerView.Adapter<RxViewHolder<VB>>() {

    private var data: List<M> = ArrayList()

    init {
        dataObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    data = it
                    notifyDataSetChanged()
                },
                {
                    val x = 1
                }
            )
            .disposedWith(lifecycleOwner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RxViewHolder<VB> {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(viewHolderLayoutId, parent, false)
        return RxViewHolder(bindView, itemView)
    }

    override fun onBindViewHolder(holder: RxViewHolder<VB>, position: Int) {
        bindItem(
            BindModel(
                holder.binding,
                data[position],
                position,
                data.size
            )
        )

        onItemClick?.let {
            holder.itemView.setOnClickListener {
                it(OnItemClickModel(data[position], position))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class RxViewHolder<VB: ViewBinding>(
    bindView: BindView<VB>,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private var _binding: VB? = null
    val binding get() = _binding!!

    init {
        _binding = bindView(itemView)
    }
}