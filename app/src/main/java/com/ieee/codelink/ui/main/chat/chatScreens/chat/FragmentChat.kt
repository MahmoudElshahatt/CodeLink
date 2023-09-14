package com.ieee.codelink.ui.main.chat.chatScreens.chat

import androidx.fragment.app.viewModels
import com.ieee.codelink.core.BaseFragment
import com.ieee.codelink.core.BaseViewModel
import com.ieee.codelink.databinding.FragmentInboxBinding

class FragmentChat : BaseFragment<FragmentInboxBinding>(FragmentInboxBinding::inflate) {
    override val viewModel : BaseViewModel by viewModels()

}