package com.layrin.smsapp.ui.conversation

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.layrin.smsapp.R
import com.layrin.smsapp.SmsApplication
import com.layrin.smsapp.common.OnItemClickListener
import com.layrin.smsapp.databinding.FragmentNewConversationBinding
import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.repository.DaoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewConversationFragment : Fragment(),
    OnItemClickListener<Contact>,
    MenuProvider {

    private var _binding: FragmentNewConversationBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragmentAdapter: ContactListRecyclerViewAdapter

    private val viewModel: NewConversationViewModel by activityViewModels {
        NewConversationViewModelFactory(
            DaoRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewConversationBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        fragmentAdapter = ContactListRecyclerViewAdapter(requireActivity(), listener = this)

        binding.rcContact.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = fragmentAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            fragmentAdapter.submitList(viewModel.getContacts().toMutableList())
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.new_conversation_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            else -> false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onItemClick(view: View, conversation: Contact) {
        findNavController().navigate(R.id.action_newConversationFragment_to_conversationFragment)
    }

    override fun mainInterface(size: Int) {}

}