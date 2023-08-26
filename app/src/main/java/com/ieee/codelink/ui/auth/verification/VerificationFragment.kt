package com.ieee.codelink.ui.auth.verification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ieee.codelink.R
import com.ieee.codelink.common.showSnackbar
import com.ieee.codelink.core.BaseFragment
import com.ieee.codelink.core.ResponseState
import com.ieee.codelink.databinding.FragmentVerificationBinding
import com.ieee.codelink.domain.models.AuthResponse
import com.ieee.codelink.ui.auth.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerificationFragment :
    BaseFragment<FragmentVerificationBinding>(FragmentVerificationBinding::inflate) {
    override val viewModel: LoginViewModel by viewModels()
    private val navArgs by navArgs<VerificationFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClicks()
        setObservers()
    }



    private fun setOnClicks() {
        setKeyboardClicks()
        binding.btnSubmit.setOnClickListener {
            lifecycleScope.launch {
                viewModel.checkOtp(binding.otpView.otp.toString() , navArgs.email)
            }
        }
    }

    private fun setObservers() {
        viewModel.checkOtpState.awareCollect {state->
            when(state) {
                is ResponseState.Success -> {
                    login(state)
                }
                is ResponseState.Loading -> {

                }
                is ResponseState.NetworkError -> {
                    showToast(getString(R.string.network_error))
                }
                is ResponseState.Error -> {
                    state.message?.let {
                        showToast(state.message.toString())
                    }
                }
                is ResponseState.Empty->{}
                else -> {
                    showSnackbar(getString(R.string.invalid_otp),requireContext() , binding.root)
                }
            }
        }
    }

    private fun setKeyboardClicks() {
        binding.keyboard.apply {
            key0.setOnClickListener {addToOtp("0")}
            key1.setOnClickListener {addToOtp("1")}
            key2.setOnClickListener {addToOtp("2")}
            key3.setOnClickListener {addToOtp("3")}
            key4.setOnClickListener {addToOtp("4")}
            key5.setOnClickListener {addToOtp("5")}
            key6.setOnClickListener {addToOtp("6")}
            key7.setOnClickListener {addToOtp("7")}
            key8.setOnClickListener {addToOtp("8")}
            key9.setOnClickListener {addToOtp("9")}
            keyBackspace.setOnClickListener {removeFromOtp()}
        }
    }

    private fun addToOtp(num: String) {
        val otpView = binding.otpView
        val otp =otpView.otp
        otpView.setOTP(otp+num)
    }
    private fun removeFromOtp() {
        val otpView = binding.otpView
        val otp =otpView.otp
        otp?.let {
            otpView.setOTP(otp.dropLast(1))
        }
    }

    private fun login(state: ResponseState.Success<AuthResponse>) {
        lifecycleScope.launch {
            state.data.let {response->
                viewModel.cacheUser(response!!.data.user)
                navigateToHome(response.data.user.token!!)
            }
        }
    }
}