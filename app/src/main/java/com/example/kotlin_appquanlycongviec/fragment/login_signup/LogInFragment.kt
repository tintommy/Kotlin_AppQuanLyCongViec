package com.example.kotlin_appquanlycongviec.fragment.login_signup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.activity.MainActivity
import com.example.kotlin_appquanlycongviec.databinding.FragmentLogInBinding
import com.example.kotlin_appquanlycongviec.request.SignUpRequest
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LogInFragment : Fragment() {
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private lateinit var binding: FragmentLogInBinding
    private lateinit var user: FirebaseUser
  
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonEvent()
        initSignInWithGoogle()
        lifecycleScope.launch {

            nguoiDungViewModel.login.collectLatest {
                when (it) {

                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                        // binding.tvThongBao.text = ""
                    }

                    is Resource.Success -> {

                        binding.btnLogin.revertAnimation()
                        val intent = Intent(
                            requireContext(),
                            MainActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        Toast.makeText(
                            requireContext(),
                            "Đăng nhập vào " + binding.edtEmail.text.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(
                            requireContext(),
                            "Sai email hoặc mật khẩu",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {}
                }


            }
        }


    }

    fun initSignInWithGoogle() {

        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser

//        if (currentUser != null) {
//            // The user is already signed in, navigate to MainActivity
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
//            requireActivity().finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
//        }


        lifecycleScope.launch {
            nguoiDungViewModel.emailExist.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        //nếu đã có tài khoản


                    }

                    is Resource.Error -> {
                        //nếu chưa có tài khoản
                        val signUpRequest = SignUpRequest(
                            user.email!!,
                            user!!.displayName!!,
                            "",
                            generatePassword(),
                            "2000-01-01",
                            true
                        )
                        nguoiDungViewModel.userSignup(signUpRequest)
                    }

                    else -> {}


                }
            }
        }


        binding.btnGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun setButtonEvent() {
        binding.btnLogin.setOnClickListener {
            if (binding.edtEmail.text.toString().equals("") || binding.edtPassword.text.toString()
                    .equals("")
            ) {
                Toast.makeText(
                    requireContext(),
                    "Hãy nhập đủ email và mật khẩu",
                    Toast.LENGTH_SHORT
                ).show()
            } else
                nguoiDungViewModel.userLogin(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString()
                )
        }

        binding.registerNow.setOnClickListener {
            it.findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }
    }


    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google sign in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser!!
                    Toast.makeText(
                        requireContext(),
                        "Signed in as ${user.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    nguoiDungViewModel.checkUserEmail(user.email!!)


                    //   startActivity(Intent(this, MainActivity::class.java))
                    // finish()
                } else {
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    fun generatePassword(): String {
        val upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowerCaseLetters = upperCaseLetters.toLowerCase(Locale.ROOT)
        val numbers = "0123456789"
        val specialCharacters = "@"

        val allowedChars = upperCaseLetters + lowerCaseLetters + numbers + specialCharacters

        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }
}