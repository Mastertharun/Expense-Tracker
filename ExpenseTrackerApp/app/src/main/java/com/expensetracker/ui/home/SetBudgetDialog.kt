package com.expensetracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.expensetracker.MainActivity
import com.expensetracker.databinding.DialogSetBudgetBinding
import com.expensetracker.viewmodel.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SetBudgetDialog : BottomSheetDialogFragment() {

    private var _binding: DialogSetBudgetBinding? = null
    private val binding get() = _binding!!
    private val factory by lazy { (requireActivity() as MainActivity).factory }
    private val viewModel: TransactionViewModel by activityViewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSetBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-fill existing budget
        viewModel.monthlyBudget.observe(viewLifecycleOwner) { budget ->
            if (budget != null && budget.amount > 0) {
                binding.etBudget.setText(budget.amount.toLong().toString())
            }
        }

        binding.btnSaveBudget.setOnClickListener {
            val amount = binding.etBudget.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilBudget.error = "Enter a valid budget amount"
                return@setOnClickListener
            }
            viewModel.saveBudget(amount)
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}