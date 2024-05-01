package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentThongKeCongViecPdfBinding

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.text.LineBreaker
import android.os.Build
//import android.os.Bundle
import android.os.Environment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.CongViecNgayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.IOException

@AndroidEntryPoint
class ThongKeCongViecPdfFragment : Fragment() {
    private lateinit var binding: FragmentThongKeCongViecPdfBinding
    var pageHeight = 1800
    //    var pageWidth = 792
    var pageWidth = 1122

    var PERMISSION_CODE = 101
    private var luaChonKieuXuatpdf = 1
    private val congViecNgayViewModel by viewModels<CongViecNgayViewModel>()
    private var currentRadioSelection: Int = R.id.rdAll
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH]
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var doList: MutableList<CongViecNgay> = mutableListOf()
    private var doneList: MutableList<CongViecNgay> = mutableListOf()
    private var notDoneList: MutableList<CongViecNgay> = mutableListOf()
    private var printingList: MutableList<CongViecNgay> = mutableListOf()
    private var printingList2: MutableList<CongViecNgay> = mutableListOf()
    private var isDataLoaded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThongKeCongViecPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initBM();

        initMonthSpinner();
        initDayOnLayoutPickDay()

        setBtnEvent()

        loadDataNgay()
        loadData()


        if (checkPermissions()) {
        } else {
            // if the permission is not granted, calling request permission method.
            requestPermission()
        }
    }

    private fun initDayOnLayoutPickDay() {
        binding.apply {
            tvDateStart.setText(dinhDangNgay(1, thang, nam))
            tvDateEnd.setText(dinhDangNgay(30, thang, nam))
        }
       loadDataNgay()
    }

    private fun loadData() {
        lifecycleScope.launch {
            congViecNgayViewModel.danhSachCongViecNgay.collectLatest {
                when (it) {
                    is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Success -> {
                        initData(it.data!!)
                        isDataLoaded = true
//                        generatePDF(printingList)
                        updatePrintingList()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Danh sách trống", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}


                }

            }
        }
    }
    private fun updatePrintingList() {
        printingList.clear()
        when (currentRadioSelection) {
            R.id.rdAll -> printingList.addAll(doList)
            R.id.rdCompleted -> printingList.addAll(doneList)
            R.id.rdNotCompleted -> printingList.addAll(notDoneList)
        }
        printingList.sortBy { it.ngayLam }
    }

    private fun initBM() {
//        bmp = BitmapFactory.decodeResource(resources, R.drawable.gg)
//        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)
    }
    private fun loadDataNgay(){
        congViecNgayViewModel.taiDanhSachCongViecTuNgayDenNgay(dinhDangNgayAPI(
            binding.tvDateStart.text.toString().substring(0, 2).toInt(),
            binding.tvDateStart.text.toString().substring(3, 5).toInt() - 1,
            binding.tvDateStart.text.toString().substring(6, 10).toInt()
        ), dinhDangNgayAPI(
            binding.tvDateEnd.text.toString().substring(0, 2).toInt(),
            binding.tvDateEnd.text.toString().substring(3, 5).toInt() - 1,
            binding.tvDateEnd.text.toString().substring(6, 10).toInt()  ) )
    }
    private fun setBtnEvent() {
        binding.layOutPickDate.setOnClickListener(View.OnClickListener {
            loadDataNgay()
        })
        binding.rdGroup.setOnCheckedChangeListener { radioGroup, i ->
            // Lưu trữ vị trí của RadioButton được chọn vào biến selectedRadioButtonId
            currentRadioSelection = i
           updatePrintingList()
        }
        binding.btnLichStart.setOnClickListener(View.OnClickListener { openLichDialog1() })
        binding.btnLichEnd.setOnClickListener(View.OnClickListener { openLichDialog2() })
        binding.apply {


            tvTheoThang.setOnClickListener {
                layOutPickMonth.visibility = View.VISIBLE
                layOutPickDate.visibility = View.GONE
                congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang+1, binding.etYear.text.toString().toInt())
            }

            tvTheoNgay.setOnClickListener {
                layOutPickMonth.visibility = View.GONE
                layOutPickDate.visibility = View.VISIBLE
                loadDataNgay()
            }

            btnGeneratePDF.setOnClickListener {
                luaChonKieuXuatpdf =1
                isDataLoaded = false
                congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang+1, binding.etYear.text.toString().toInt())
                loadData()
                updatePrintingList()
                generatePDF(printingList)


            }

            btnGeneratePDFNgay.setOnClickListener {
                luaChonKieuXuatpdf = 2
                isDataLoaded = false
                loadDataNgay()
                loadData()
                updatePrintingList()
                generatePDF(printingList)




            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generatePDF(list: List<CongViecNgay>) {
        if (list.isEmpty()) {
            Toast.makeText(requireContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val title = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var currentPage: PdfDocument.Page? = null

        try {
            currentPage = pdfDocument.startPage(pageInfo)
            val canvas = currentPage.canvas

            // Setting up title paint
            title.color = Color.BLUE
            title.textSize = 50F
            title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            // Drawing title
            val titleText = "Thống kê công việc"
            val titleWidth = title.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2F
            val titleY = 80F
            canvas.drawText(titleText, titleX, titleY, title)

            // Setting up colors and text size for content
            paint.color = Color.BLACK
            paint.textSize = 16F

            // Calculate line height
            val lineHeight = 40

            // Draw headers
            val headers = listOf("STT", "Ngày", "Công việc", "Tính chất", "Mô tả", "Trạng thái")
            val startX = 56F
            val startY = titleY + 80F // Start after title
            val columnWidth = (pageWidth - 2 * startX) / headers.size // Chiều rộng của mỗi cột

            // Setting up paint for headers
            val headerPaint = Paint()
            headerPaint.color = Color.BLUE
            headerPaint.textSize = 24F // Tăng kích thước của tiêu đề
            headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            headers.forEachIndexed { index, header ->
                val headerWidth = headerPaint.measureText(header) // Chiều rộng thực của tiêu đề
                val headerX = startX + index * columnWidth + (columnWidth - headerWidth) / 2 // Tính toán vị trí căn giữa
                canvas.drawText(header, headerX, startY, headerPaint)
            }

            // Draw content
            var currentY = startY + lineHeight
            var dem = 1
            for (cv in list) {
                val content = listOf(
                    dem.toString(),
                    convertToDDMMYYYY(cv.ngayLam),
                    cv.congViec.tieuDe,
                    tinhChatToString(cv.congViec.tinhChat),
                    cv.congViec.noiDung,
                    setTrangThaiString(cv.trangThai)
                )
                content.forEachIndexed { index, text ->
                    val x = startX + index * columnWidth
                    // Đặt màu cho các giá trị tùy thuộc vào giá trị của tinhChat
                    val color = when (index) {
                        3 -> when (cv.congViec.tinhChat) {
                            0 -> Color.BLACK
                            1 -> Color.GREEN // Màu xanh lá cây cho tinhChat = 1 (Quan trọng)
                            else -> Color.RED // Màu đỏ cho tinhChat = 2 (Rất quan trọng)
                        }
                        else -> Color.BLACK // Màu đen cho các cột khác
                    }
                    paint.color = color
                    if (paint.measureText(text) > columnWidth) {
                        // If content exceeds column width, draw text on multiple lines
                        drawText(canvas, text, x, currentY, columnWidth, paint)
                        currentY += lineHeight * 2 // Increase line height
                    } else {
                        // If content does not exceed column width, draw text on one line
                        canvas.drawText(text, x, currentY, paint)
                    }
                }
                currentY += lineHeight // Move to next line for next row
                dem += 1
            }
        } finally {
            if (currentPage != null) {
                pdfDocument.finishPage(currentPage)
            }

            // Save the PDF document
            val file = File(Environment.getExternalStorageDirectory(), "ThongKeCV.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(requireContext(), "Tạo file PDF thành công", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Thất bại khi tạo file PDF", Toast.LENGTH_SHORT).show()
            } finally {
                pdfDocument.close()
            }
        }
    }




    private fun drawText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
        val textPaint = TextPaint(paint)
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, maxWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0F, 1F)
            .setIncludePad(false)
            .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
            .build()
        canvas.save()
        canvas.translate(x, y)
        layout.draw(canvas)
        canvas.restore()
    }

    private fun tinhChatToString(tinhChat: Int): String {
        return when (tinhChat) {
            0 -> "Bình thường"
            1 -> "Quan trọng"
            else -> "Rất quan trọng"
        }
    }



    private fun checkPermissions(): Boolean {
        // writing to external storage permission
        var writeStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            WRITE_EXTERNAL_STORAGE
        )

        // reading external storage permission
        var readStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        )

        // returning true if both the permissions are granted and returning false if permissions are not granted.
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        // requesting read and write to storage permission for our application.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    //  calling on request permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            // ochecking if result size is > 0
            if (grantResults.size > 0) {

                //check permission is granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    // if permissions are granted we are displaying a toast message.
                    Toast.makeText(requireContext(), "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {

                    Toast.makeText(requireContext(), "Permission Denied..", Toast.LENGTH_SHORT).show()
//                    finish()
                }
            }
        }
    }

    private fun initMonthSpinner() {
        val luaChon = arrayOf(
            "Tháng 1",
            "Tháng 2",
            "Tháng 3",
            "Tháng 4",
            "Tháng 5",
            "Tháng 6",
            "Tháng 7",
            "Tháng 8",
            "Tháng 9",
            "Tháng 10",
            "Tháng 11",
            "Tháng 12"
        )
        val adapter = ArrayAdapter(requireActivity(), R.layout.remind_spinner_item, luaChon)
        binding.spMonth.setAdapter(adapter)
        binding.spMonth.setSelection(thang)

        binding.spMonth.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (view != null) {
                    thang = position
                    congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang+1, binding.etYear.text.toString().toInt())
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
    }



    private fun initData(cvList: List<CongViecNgay>) {
        doList.clear()
        doneList.clear()
        notDoneList.clear()
        doList.addAll(cvList)
        doneList.addAll(cvList.filter { it.trangThai == true })
        notDoneList.addAll(cvList.filter { it.trangThai == false })

    }
    private fun convertToDDMMYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        var date: Date? = null
        date = try {
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
        return outputFormat.format(date)
    }

    // Hàm chuyển đổi từ "dd-MM-yyyy" sang "yyyy-MM-dd"
    private fun convertToYYYYMMDD(date: String): String {
        val originalFormat = SimpleDateFormat("dd-MM-yyyy")
        val targetFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateObj: Date
        var formattedDate = ""
        try {
            dateObj = originalFormat.parse(date)
            formattedDate = targetFormat.format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedDate
    }

    private fun setTrangThaiString(trangThai: Boolean): String {
        return if (trangThai) "Đã hoàn thành" else "Chưa hoàn thành"
    }


    private fun openLichDialog1() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.tvDateStart.setText(dinhDangNgay(day, month, year))
                ngay = day
                thang = month
                nam = year
            }, nam, thang, ngay
        )
        dialog.show()
    }
    private fun openLichDialog2() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.tvDateEnd.setText(dinhDangNgay(day, month, year))
                ngay = day
                thang = month
                nam = year
            }, nam, thang, ngay
        )
        dialog.show()
    }

    private fun dinhDangNgay(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        temp += "/"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "/"
        temp += nam
        return temp
    }


    private fun dinhDangNgayAPI(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += nam
        temp += "-"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "-"
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        return temp
    }


}


