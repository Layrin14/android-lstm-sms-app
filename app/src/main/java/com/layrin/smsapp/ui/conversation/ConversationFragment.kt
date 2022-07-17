package com.layrin.smsapp.ui.conversation

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
import androidx.navigation.fragment.navArgs
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.layrin.smsapp.R
import com.layrin.smsapp.common.ActionModeCallback
import com.layrin.smsapp.common.OnItemClickListener
import com.layrin.smsapp.common.SimpleCallback
import com.layrin.smsapp.databinding.FragmentConversationBinding
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message
import com.layrin.smsapp.repository.DaoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversationFragment : Fragment(),
    SimpleCallback,
    OnItemClickListener<Message>,
    MenuProvider {

    private val viewModel: ConversationViewModel by activityViewModels {
        ConversationViewModelFactory(
            DaoRepository()
        )
    }

    private val navArgs: ConversationFragmentArgs by navArgs()

    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!

    private lateinit var actionModeCallback: ActionModeCallback
    private var actionMode: ActionMode? = null

    private var scrollToBottom = true

    private lateinit var fragmentAdapter: MessageRecyclerViewAdapter
    private lateinit var fragmentLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.init(navArgs.conversation)

        lifecycleScope.launch(Dispatchers.IO) {
            (activity as AppCompatActivity).supportActionBar?.title =
                if (viewModel.getContactName() != null) viewModel.getContactName()
                else navArgs.conversation.number
        }

        getConversation()
        setupRecyclerView()

        actionModeCallback = ActionModeCallback(this, R.menu.message_selection)
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            viewModel.getAllMessages(navArgs.conversation.number).collect { item ->
                item.forEach {
                    if (it.conversation.number == navArgs.conversation.number) {
                        viewModel.messages = it.message
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (scrollToBottom) {
                    scrollToBottom = false
                    val id = viewModel.getMessage(navArgs.conversation)
                    if (id?.id != -1) {
                        binding.rcMessage.postDelayed({
                            scrollToItem(id.toString().toInt())
                        }, 200)
                    }
                }
            }
        }

        fragmentAdapter = MessageRecyclerViewAdapter(requireActivity(), listener = this)

        binding.rcMessage.apply {
            fragmentLayoutManager = LinearLayoutManager(requireActivity()).apply {
                orientation = LinearLayoutManager.VERTICAL
                reverseLayout = true
            }
            layoutManager = fragmentLayoutManager
            adapter = fragmentAdapter
        }

        lifecycleScope.launch {
            viewModel.pagingData.cachedIn(lifecycleScope).collectLatest {
                fragmentAdapter.submitData(it)
            }
        }

        fragmentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (fragmentLayoutManager.findFirstVisibleItemPosition() == 0) {
                    binding.rcMessage.scrollToPosition(0)
                }
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                if (fragmentLayoutManager.findFirstVisibleItemPosition() == 0) {
                    binding.rcMessage.scrollToPosition(0)
                } else binding.rcMessage.smoothScrollToPosition(0)
            }
        })
    }

    private fun getConversation() {
        viewModel.conversation = navArgs.conversation
    }

    private fun scrollToItem(id: Int) {
        val index = viewModel.messages.indexOfFirst { m ->
            m.id == id
        }

        binding.rcMessage.apply {
            scrollToPosition(index)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        smoothScrollBy(0, measuredHeight / 2 - getChildAt(index).bottom)
                        removeOnScrollListener(this)
                    }
                }
            })
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.conversation_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_more -> {
                Toast.makeText(requireActivity(), "more", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    override fun onActionItemClicked(item: MenuItem) {
        when (item.itemId) {
            R.id.action_copy -> {
                fragmentAdapter.close()
                actionMode?.finish()
                Toast.makeText(requireActivity(), "copy", Toast.LENGTH_SHORT).show()
            }
            R.id.action_delete -> {
                fragmentAdapter.close()
                actionMode?.finish()
                Toast.makeText(requireActivity(), "delete", Toast.LENGTH_SHORT).show()
            }
            android.R.id.home -> {
                fragmentAdapter.close()
                actionMode?.finish()
            }
        }
    }

    override fun mainInterface(size: Int) {
        if (actionMode == null) actionMode = requireActivity().startActionMode(actionModeCallback)
        if (size > 0) actionMode?.title = "$size"
        else actionMode?.finish()
    }

    override fun onDestroyActionMode() {
        fragmentAdapter.close()
        actionMode = null
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateActionMode() {}

    override fun onPrepareActionMode() {}

    override fun onItemClick(view: View, conversation: Message) {}

}