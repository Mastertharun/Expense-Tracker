package com.expensetracker.ui.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.R
import com.expensetracker.data.db.entities.Wallet
import com.expensetracker.databinding.ItemWalletCardBinding
import java.text.NumberFormat
import java.util.Locale

private const val TYPE_WALLET = 0
private const val TYPE_ADD    = 1

class WalletCardAdapter(
    private val onWalletClick: (Wallet) -> Unit,
    private val onAddClick: () -> Unit
) : ListAdapter<Wallet, RecyclerView.ViewHolder>(DiffCallback) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // We always append one extra item for the "+" card
    override fun getItemCount() = super.getItemCount() + 1

    override fun getItemViewType(position: Int) =
        if (position < super.getItemCount()) TYPE_WALLET else TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_WALLET) {
            val binding = ItemWalletCardBinding.inflate(inflater, parent, false)
            WalletViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.item_wallet_add, parent, false)
            AddViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WalletViewHolder) {
            holder.bind(getItem(position))
        } else {
            (holder as AddViewHolder).itemView.setOnClickListener { onAddClick() }
        }
    }

    inner class WalletViewHolder(private val binding: ItemWalletCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: Wallet) {
            binding.tvWalletName.text = wallet.name
            binding.tvWalletBalance.text = currencyFormat.format(wallet.balance)
            binding.root.setOnClickListener { onWalletClick(wallet) }
        }
    }

    class AddViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)

    companion object DiffCallback : DiffUtil.ItemCallback<Wallet>() {
        override fun areItemsTheSame(a: Wallet, b: Wallet) = a.id == b.id
        override fun areContentsTheSame(a: Wallet, b: Wallet) = a == b
    }
}
