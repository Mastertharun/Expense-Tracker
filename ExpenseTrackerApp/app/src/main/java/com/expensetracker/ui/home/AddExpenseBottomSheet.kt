package com.expensetracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.expensetracker.MainActivity
import com.expensetracker.data.db.entities.Wallet
import com.expensetracker.data.db.entities.TransactionCategory
import com.expensetracker.databinding.BottomSheetAddExpenseBinding
import com.expensetracker.viewmodel.TransactionViewModel
import com.expensetracker.viewmodel.WalletViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.util.Calendar

class AddExpenseBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddExpenseBinding? = null
    private val binding get() = _binding!!

    private val factory by lazy { (requireActivity() as MainActivity).factory }
    private val viewModel: TransactionViewModel by activityViewModels { factory }
    private val walletViewModel: WalletViewModel by activityViewModels { factory }

    private var selectedCategory: String = TransactionCategory.FOOD.name
    private var selectedDate: Long = System.currentTimeMillis()

    // Wallet list received from LiveData
    private var walletList: List<Wallet> = emptyList()
    private var selectedWalletIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Category chips ────────────────────────────────────────────────────
        TransactionCategory.values().forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = "${cat.emoji} ${cat.displayName}"
                isCheckable = true
                isChecked = cat.name == selectedCategory
                setOnCheckedChangeListener { _, checked ->
                    if (checked) selectedCategory = cat.name
                }
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    requireContext().getColor(com.expensetracker.R.color.chip_background)
                )
            }
            binding.chipGroupCategories.addView(chip)
        }

        // ── Wallet spinner ────────────────────────────────────────────────────
        walletViewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletList = wallets
            val names = wallets.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spinnerWallet.adapter = adapter
            binding.spinnerWallet.setSelection(0)
        }

        binding.spinnerWallet.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long
                ) { selectedWalletIndex = pos }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

        // ── Date picker ───────────────────────────────────────────────────────
        binding.btnPickDate.setOnClickListener { showDatePicker() }
        updateDateButton()

        // ── Save ──────────────────────────────────────────────────────────────
        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            if (amountStr.isBlank()) {
                binding.tilAmount.error = "Please enter an amount"
                return@setOnClickListener
            }
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }
            if (walletList.isEmpty()) {
                binding.tilAmount.error = "Please create a wallet first"
                return@setOnClickListener
            }
            val note = binding.etNote.text.toString()
            val type = if (binding.rgType.checkedRadioButtonId == binding.rbExpense.id)
                "EXPENSE" else "INCOME"
            val walletId = walletList[selectedWalletIndex].id

            viewModel.addTransaction(amount, selectedCategory, note, selectedDate, type, walletId)
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
        android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                cal.set(year, month, day)
                selectedDate = cal.timeInMillis
                updateDateButton()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateButton() {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        binding.btnPickDate.text = sdf.format(java.util.Date(selectedDate))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}