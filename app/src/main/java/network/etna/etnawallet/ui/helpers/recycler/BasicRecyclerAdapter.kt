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

typealias OnItemClickWithPosition<M, VB> = (M, VB, Int) -> Unit
typealias BindItemWithPosition<M, VB> = (M, VB, Int) -> Unit

class BasicRecyclerAdapter<VB: ViewBinding, M>(
    private val viewHolderLayoutId: Int,
    private val bindView: BindView<VB>,
    private val data: List<M>,
    private val bindItem: BindItemWithPosition<M, VB>,
    private val onItemClickWithPosition: OnItemClickWithPosition<M, VB>? = null
) : RecyclerView.Adapter<RxViewHolder<VB>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RxViewHolder<VB> {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(viewHolderLayoutId, parent, false)
        return RxViewHolder(bindView, itemView)
    }

    override fun onBindViewHolder(holder: RxViewHolder<VB>, position: Int) {
        bindItem(data[position], holder.binding, position)
        holder.itemView.setOnClickListener {
            onItemClickWithPosition?.invoke(data[position], holder.binding, position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
