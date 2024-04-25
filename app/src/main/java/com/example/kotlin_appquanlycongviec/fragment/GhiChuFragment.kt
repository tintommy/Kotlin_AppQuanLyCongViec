package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.DialogChangePinBinding
import com.example.kotlin_appquanlycongviec.databinding.FragmentGhiChuBinding
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GhiChuFragment : Fragment() {
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private lateinit var binding: FragmentGhiChuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGhiChuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nguoiDungViewModel.getUser()
        lifecycleScope.launch {
            nguoiDungViewModel.user.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        if (it.data!!.maPin == null) {
                            findNavController().navigate(R.id.action_ghiChuFragment_to_taoMaPinGhiChuFragment)
                        }


                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }


        }

        lifecycleScope.launch {
            nguoiDungViewModel.pin.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnConfirm.revertAnimation()
                        if (it.data!!.status.equals("true"))
                            findNavController().navigate(R.id.action_ghiChuFragment_to_dsGhiChuFragment)
                        else
                            Toast.makeText(requireContext(), "Sai mã pin", Toast.LENGTH_SHORT).show()

                    }

                    is Resource.Error -> {
                        binding.btnConfirm.revertAnimation()
                        Toast.makeText(requireContext(), it.data.toString(), Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        binding.apply {
            btnConfirm.setOnClickListener {

                nguoiDungViewModel.checkPin(et1.text.toString() + et2.text.toString() + et3.text.toString() + et4.text.toString() + et5.text.toString() + et6.text.toString())
            }

            btnForgetPin.setOnClickListener {
               setDialogEvent()
            }

        }
    }

    private fun setDialogEvent() {
        val dialogBinding: DialogChangePinBinding =DialogChangePinBinding.inflate(layoutInflater)

        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDialog.show()


        var pass=""
        var pin=""
        var rePin=""
        var check=0
        dialogBinding.apply {
            dialogBinding.btnXacNhan.setOnClickListener {
                check=0
                tvSaiPass.visibility= View.GONE
                tvSaiPin.visibility= View.GONE
                tvSaiRePin.visibility= View.GONE
                pass=etPass.text.toString()
                pin=etPin.text.toString()
                rePin=etRePin.text.toString()
                if(pin.length!=6){
                    check=1
                    tvSaiPin.visibility= View.VISIBLE
                }

                if(!pin.equals(rePin)){
                    check=1
                    tvSaiRePin.visibility= View.VISIBLE
                }
                if(check==0){
                    nguoiDungViewModel.doiPin(pass,pin)
                }
            }
        }

        lifecycleScope.launch{
            nguoiDungViewModel.doiPin.collectLatest {

                when(it){
                    is Resource.Loading ->{
                        dialogBinding.btnXacNhan.startAnimation()
                    }
                    is Resource.Success ->{
                        Toast.makeText(requireContext(), "Đổi PIN thành công", Toast.LENGTH_LONG).show()
                        mDialog.dismiss()

                    }
                    is Resource.Error ->{
                        dialogBinding.btnXacNhan.revertAnimation()
                        if(it.message.equals("404"))
                            dialogBinding.tvSaiPass.visibility=View.VISIBLE
                        else{
                            Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show()
                        }

                    }
                    else ->{}
                }

            }
        }

    }

}