package com.layrin.smsapp.ui.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.layrin.smsapp.R
import com.layrin.smsapp.common.ActionModeCallback
import com.layrin.smsapp.common.OnItemClickListener
import com.layrin.smsapp.common.SimpleCallback
import com.layrin.smsapp.databinding.FragmentMainBinding
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.repository.DaoRepository
import com.layrin.smsapp.repository.ProviderRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment(),
    SimpleCallback,
    OnItemClickListener<Conversation>,
    MenuProvider {

    companion object {
        fun newInstance(label: Int): MainFragment {
            return MainFragment().apply {
                arguments = Bundle().apply { putInt(argsLabel, label) }
            }
        }
    }

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            ProviderRepository(requireContext()),
            DaoRepository(),
            requireActivity().application
        )
    }

    private val argsLabel = "LABEL"
    private var categoryLabel = 0

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var actionModeCallback: ActionModeCallback
    private var actionMode: ActionMode? = null

    private lateinit var fragmentAdapter: ConversationRecyclerViewAdapter

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if (positionStart == 0) binding.rcMain.scrollToPosition(0)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            if (toPosition == 0) binding.rcMain.scrollToPosition(0)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.providerRepository.smsManager.updateAsync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateContactAsync()
        arguments?.apply {
            categoryLabel = getInt(argsLabel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val flow =
            viewModel.getCategoryConversation()[categoryLabel].cachedIn(lifecycleScope)

        fragmentAdapter = ConversationRecyclerViewAdapter(requireActivity(), listener = this)

        fragmentAdapter.registerAdapterDataObserver(dataObserver)

        binding.rcMain.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = fragmentAdapter
            smoothScrollToPosition(0)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            flow.collectLatest {
                fragmentAdapter.submitData(it)
            }
        }

        actionModeCallback = ActionModeCallback(this, R.menu.conversation_selection)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                Toast.makeText(requireActivity(), "search", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                Toast.makeText(requireActivity(), "about", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                Toast.makeText(requireActivity(), "settings", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    override fun onActionItemClicked(item: MenuItem) {
        val labelItems = arrayOf(
            getString(R.string.normal_label),
            getString(R.string.fraud_label),
            getString(R.string.promotion_label)
        )
        val selectedItem = fragmentAdapter.selectedItem
        val alertDialog = MaterialAlertDialogBuilder(requireActivity())
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        var checkedItem = 0
        when (item.itemId) {
            R.id.action_move -> {
                alertDialog.setTitle(getString(R.string.change_label_title))
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        Toast.makeText(requireActivity(),
                            "$checkedItem dipilih",
                            Toast.LENGTH_SHORT).show()
                        selectedItem.forEach { data ->
                            viewModel.updateLabel(data, checkedItem)
                        }
                        dialog.dismiss()
                        actionMode?.finish()
                        fragmentAdapter.close()
                    }.setSingleChoiceItems(labelItems, 0) { _, selected ->
                        checkedItem = selected
                    }.show()
            }
            R.id.action_delete -> {
                actionMode?.finish()
                fragmentAdapter.close()
            }
            android.R.id.home -> {
                actionMode?.finish()
                fragmentAdapter.close()
            }
        }
    }

    override fun onDestroyActionMode() {
        if (fragmentAdapter.isActive) fragmentAdapter.close()
        actionMode = null
    }

    override fun onItemClick(view: View, conversation: Conversation) {
        val action =
            MainFragmentDirections.actionMainFragmentToConversationFragment(conversation)
        findNavController().navigate(action)
        viewModel.updateRead(conversation)
    }

    override fun mainInterface(size: Int) {
        if (actionMode == null) actionMode = requireActivity().startActionMode(actionModeCallback)
        if (size > 0) actionMode?.title = "$size"
        else actionMode?.finish()
    }

    override fun onCreateActionMode() {}

    override fun onPrepareActionMode() {}

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}