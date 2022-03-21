package network.etna.etnawallet.ui.asset.assetinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.android.disposebag.disposedWith
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import network.etna.etnawallet.databinding.ViewholderAssetMarketInfoLineBinding
import network.etna.etnawallet.databinding.ViewholderAssetMarketInfoRowBinding
import network.etna.etnawallet.databinding.ViewholderAssetMarketInfoTitleBinding
import network.etna.etnawallet.ui.helpers.recycler.RxViewHolder

sealed class AssetInfoMarketData {
    object Line : AssetInfoMarketData()
    class Row(val title: String, val value: String) : AssetInfoMarketData()
    class Title(val title: String) : AssetInfoMarketData()
}

class AssetInfoMarketRecyclerAdapter(
    lifecycleOwner: LifecycleOwner,
    private val dataObservable: Flowable<List<AssetInfoMarketData>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val LINE_VIEW_HOLDER_TYPE: Int = 0
        private const val ROW_VIEW_HOLDER_TYPE: Int = 1
        private const val TITLE_VIEW_HOLDER_TYPE: Int = 2
    }

    private var data: List<AssetInfoMarketData> = ArrayList()

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
                })
            .disposedWith(lifecycleOwner)
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is AssetInfoMarketData.Line -> LINE_VIEW_HOLDER_TYPE
            is AssetInfoMarketData.Row -> ROW_VIEW_HOLDER_TYPE
            is AssetInfoMarketData.Title -> TITLE_VIEW_HOLDER_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LINE_VIEW_HOLDER_TYPE -> {
                val itemView = ViewholderAssetMarketInfoLineBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                RxViewHolder(
                    ViewholderAssetMarketInfoLineBinding::bind,
                    itemView.root
                )
            }
            ROW_VIEW_HOLDER_TYPE -> {
                val itemView = ViewholderAssetMarketInfoRowBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                RxViewHolder(
                    ViewholderAssetMarketInfoRowBinding::bind,
                    itemView.root
                )
            }
            TITLE_VIEW_HOLDER_TYPE -> {
                val itemView = ViewholderAssetMarketInfoTitleBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                RxViewHolder(
                    ViewholderAssetMarketInfoTitleBinding::bind,
                    itemView.root
                )
            }
            else -> throw IllegalArgumentException("$viewType not supported")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val model = data[position]) {
            is AssetInfoMarketData.Line -> {
            }
            is AssetInfoMarketData.Row -> {
                val viewHolder = holder as RxViewHolder<ViewholderAssetMarketInfoRowBinding>
                viewHolder.binding.titleTextView.text = model.title
                viewHolder.binding.valueTextView.text = model.value
            }
            is AssetInfoMarketData.Title -> {
                val viewHolder = holder as RxViewHolder<ViewholderAssetMarketInfoTitleBinding>
                viewHolder.binding.titleTextView.text = model.title
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}