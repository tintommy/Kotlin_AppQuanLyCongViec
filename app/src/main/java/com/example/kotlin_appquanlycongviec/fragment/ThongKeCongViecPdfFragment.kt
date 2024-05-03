
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
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
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
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class ThongKeCongViecPdfFragment : Fragment() {
    private var alertDialog: AlertDialog? = null
    private lateinit var binding: FragmentThongKeCongViecPdfBinding
    var PERMISSION_CODE = 101
    private var luaChonKieuXuatpdf = 1
    private var luaChonXuatTatCaCV = 0
    private val congViecNgayViewModel by viewModels<CongViecNgayViewModel>()
    private var currentRadioSelection: Int = R.id.rdAll
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH]
    private var thang2 = calendar[Calendar.MONTH]
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var doList: MutableList<CongViecNgay> = mutableListOf()
    private var doneList: MutableList<CongViecNgay> = mutableListOf()
    private var notDoneList: MutableList<CongViecNgay> = mutableListOf()
    private var printingList: MutableList<CongViecNgay> = mutableListOf()
    private var isDataLoaded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThongKeCongViecPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMonthSpinner();
        initDayOnLayoutPickDay()

        setBtnEvent()

        if (checkPermissions()) {
        } else {
            requestPermission()
        }
    }

    private fun initDayOnLayoutPickDay() {
        binding.apply {
            tvDateStart.setText(dinhDangNgay(1, thang2, nam))
            tvDateEnd.setText(dinhDangNgay(30, thang2, nam))
        }
    }


    private fun updatePrintingList() {
        printingList.clear()
        when (currentRadioSelection) {
            R.id.rdAll ->{
                printingList.addAll(doList)
                luaChonXuatTatCaCV = 0
            }
            R.id.rdCompleted -> {
                printingList.addAll(doneList)
                luaChonXuatTatCaCV = 1
            }
            R.id.rdNotCompleted -> {
                printingList.addAll(notDoneList)
                luaChonXuatTatCaCV = 2
            }
        }
        printingList.sortBy { it.ngayLam }
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
//        binding.layOutPickDate.setOnClickListener(View.OnClickListener {
//            loadDataNgay()
//        })
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
//                congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(calendar[Calendar.MONTH]+1, binding.etYear.text.toString().toInt())
            }

            tvTheoNgay.setOnClickListener {
                layOutPickMonth.visibility = View.GONE
                layOutPickDate.visibility = View.VISIBLE
//                loadDataNgay()
            }

            btnGeneratePDF.setOnClickListener {
                luaChonKieuXuatpdf =1
                isDataLoaded = false
                initMonthSpinner()
                congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang+1, binding.etYear.text.toString().toInt())
//                loadData()
                lifecycleScope.launch {
                    congViecNgayViewModel.danhSachCongViecNgay.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                            }

                            is Resource.Success -> {
                                initData(it.data!!)
                                updatePrintingList()
                                generatePDF(printingList)
                            }

                            is Resource.Error -> {
                                Toast.makeText(requireContext(), "Danh sách trống", Toast.LENGTH_SHORT).show()
                            }

                            else -> {}

                        }

                    }
                }
            }

            btnGeneratePDFNgay.setOnClickListener {
                luaChonKieuXuatpdf = 2
                isDataLoaded = true
                loadDataNgay()
                lifecycleScope.launch {
                    congViecNgayViewModel.danhSachCongViecNgay.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                            }

                            is Resource.Success -> {
                                initData(it.data!!)
                                updatePrintingList()
                                generatePDF(printingList)
                            }

                            is Resource.Error -> {
                                Toast.makeText(requireContext(), "Danh sách trống", Toast.LENGTH_SHORT).show()
                            }

                            else -> {}

                        }

                    }
                }

            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generatePDF(list: List<CongViecNgay>) {
        if (list.isEmpty()) {
            Toast.makeText(requireContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show()
            return
        }
        var file: File? = null
        var pageHeight = 1800
        var pageWidth = 1400
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val lineHeight = 40
//        val pageHeight1 = lineHeight * (list.size + 5) +300// Calculate the page height
//        if (pageHeight1>pageHeight){
//            pageHeight = pageHeight1
//        }
        var pageIndex =1
        var currentY = 0F
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var currentPage: PdfDocument.Page? = null
        var isPageEnded = false

        try {
            currentPage = pdfDocument.startPage(pageInfo)
            var canvas = currentPage.canvas
            // pain for title
            paint.color = Color.BLUE
            paint.textSize = 50F
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            // Tieu de
            val titleText = "Danh sách công việc"
            val titleWidth = paint.measureText(titleText)
            val titleX = (pageWidth - titleWidth) / 2F
            val titleY = 80F
            canvas.drawText(titleText, titleX, titleY, paint)
            // Thong tin thoi gian
            currentY = titleY + 50F
            paint.textSize= 35F
            paint.color = Color.BLUE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            when (luaChonKieuXuatpdf) {
                1 -> {
                    val monthYearText = "Trong tháng ${thang+1}/${binding.etYear.text}"
                    val addInforWidth =paint.measureText(monthYearText)
                    val addInforX = (pageWidth - addInforWidth) / 2F
                    canvas.drawText(monthYearText, addInforX, currentY, paint)
                    currentY+=60F
                }
                2 -> {
                    val dateRangeText = "Từ ngày ${binding.tvDateStart.text} đến ngày ${binding.tvDateEnd.text}"
                    val addInforWidth =paint.measureText(dateRangeText)
                    val addInforX = (pageWidth - addInforWidth) / 2F
                    canvas.drawText(dateRangeText, addInforX, currentY, paint)
                    currentY+=60F
                }

            }
            currentY+=10F
            paint.textSize = 25F
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val totalText = "Tổng số công việc: ${list.size}"
            val completedText = "Tổng số công việc hoàn thành: ${list.filter { it.trangThai == true }.size}"
            val notCompletedText = "Tổng số công việc chưa hoàn thành: ${list.filter { it.trangThai == false }.size}"
            val normalText = "Tổng số công việc bình thường: ${list.filter { it.congViec.tinhChat == 0 }.size}"
            val importantText = "Tổng số công việc quan trọng: ${list.filter { it.congViec.tinhChat == 1 }.size}"
            val veryImportantText = "Tổng số công việc rất quan trọng: ${list.filter { it.congViec.tinhChat == 2 }.size}"
            when (luaChonXuatTatCaCV){
                0 -> {
                    canvas.drawText(totalText, 10F, currentY, paint)
                    canvas.drawText(completedText, 10F, currentY + 35, paint)
                    canvas.drawText(notCompletedText, 10F, currentY + 70, paint)
                    canvas.drawText(normalText, 10F, currentY + 105, paint)
                    canvas.drawText(importantText, 10F, currentY + 140, paint)
                    canvas.drawText(veryImportantText, 10F, currentY + 175, paint)
                    currentY+=240F
                }
                1 -> {
                    canvas.drawText(completedText, 10F, currentY + 35, paint)
                    canvas.drawText(normalText, 10F, currentY + 70, paint)
                    canvas.drawText(importantText, 10F, currentY + 105, paint)
                    canvas.drawText(veryImportantText, 10F, currentY + 140, paint)
                    currentY+=195F
                }
                2 -> {
                    canvas.drawText(notCompletedText, 10F, currentY + 35, paint)
                    currentY+=80F
                }
            }

            // Draw headers
            val headers = listOf( "STT","Ngày", "Công việc", "Tính chất", "Mô tả", "Trạng thái")
            val startX = 16F
            val startY = currentY
            val columnWidth = (pageWidth - 2 * startX) / headers.size

            // headers
            val headerPaint = Paint()
            headerPaint.color = Color.BLUE
            headerPaint.textSize = 26F
            headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            var startX1 =startX
            headers.forEachIndexed { index, header ->
                when (index){
                    0 -> {
                        canvas.drawText(header, startX1, startY, headerPaint)
                        startX1 += columnWidth/2
                    }
                    4 -> {
                        canvas.drawText(header, startX1, startY, headerPaint)
                        startX1 += columnWidth+columnWidth/2
                    }
                    else -> {
                        canvas.drawText(header, startX1, startY, headerPaint)
                        startX1 += columnWidth
                    }

                }

//                val headerWidth = headerPaint.measureText(header)
//                val headerX = startX + index * columnWidth + (columnWidth - headerWidth) / 2 // căn giữa
//                val headerX = startX + index * columnWidth //KO CAN GIUA
//                when (index) {
//                    0 -> canvas.drawText(header, headerX, startY, headerPaint)
//
//                    else -> canvas.drawText(header, startX + index * columnWidth -columnWidth/2, startY, headerPaint)
//                }
//
//                canvas.drawText(header, headerX, startY, headerPaint)
            }

            //content
            currentY = startY + lineHeight
            paint.color = Color.BLACK
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
                    var x = 0F
                    when (index){
                        0 -> x = startX
                        1 -> x = startX + columnWidth/2
                        5 -> x = startX + index * columnWidth
                        else -> x = startX + index * columnWidth - columnWidth/2

                    }
//                    val x = startX + index * columnWidth
                    when (index) {
                        3 -> when (cv.congViec.tinhChat) {
                            0 -> paint.color = Color.BLACK
                            1 -> paint.color = Color.GREEN
                            else -> paint.color = Color.RED
                        }
                        else -> paint.color = Color.BLACK
                    }
                    val color = when (index) {
                        3 -> when (cv.congViec.tinhChat) {
                            0 -> Color.BLACK
                            1 -> Color.GREEN
                            else -> Color.RED
                        }
                        else -> Color.BLACK
                    }
                    paint.color = color
                    if (paint.measureText(text) > columnWidth) {
                        // If content exceeds column width, draw text on multiple lines
                        if (index == 4) {
                            drawText(canvas, text, x, currentY, columnWidth+columnWidth/2, paint)
                            currentY += lineHeight * 2
                        } else {
                            drawText(canvas, text, x, currentY, columnWidth, paint)
                            currentY += lineHeight * 2
                        }
//                        drawText(canvas, text, x, currentY, columnWidth, paint)
//                        currentY += lineHeight * 2
                    } else {
                        // If content does not exceed column width, draw text on one line
                        canvas.drawText(text, x, currentY, paint)
                    }
                }
                currentY += lineHeight
                if (currentY + lineHeight > pageHeight) {
                    pdfDocument.finishPage(currentPage)
                    pageIndex++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create()
                    currentPage = pdfDocument.startPage(pageInfo) // Start a new page
                    currentY = 0F
                    canvas = currentPage.canvas

                }
                dem += 1
            }
        } finally {
            if (currentPage != null) {
                pdfDocument.finishPage(currentPage)
            }

            val sdf = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault())
            val currentTime = Date()
            val formattedTime = sdf.format(currentTime)
            val fileName = "danh sach cong viec-$formattedTime.pdf"

            // Save the PDF document
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            try {
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(requireContext(), "Tạo file PDF thành công, lưu ở Download", Toast.LENGTH_SHORT).show()
                showOpenPdfAlertDialog(file)

                alertDialog = null
              


            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Thất bại khi tạo file PDF", Toast.LENGTH_SHORT).show()
            } finally {
                pdfDocument.close()
            }

        }


    }
    private fun showOpenPdfAlertDialog(file: File) {
        if(alertDialog == null) {
            var builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Xuất file thành công")
            builder.setMessage("Bạn có muốn xem file PDF vừa xuất không?")
            builder.setPositiveButton("Xem") { dialog, which ->
//                val intent = Intent(Intent.ACTION_VIEW)
//                val uri = FileProvider.getUriForFile(
//                    requireContext(),
//                    requireContext().applicationContext.packageName + ".fileprovider",
//                    file
//                )
//                intent.setDataAndType(uri, "application/pdf")
//                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                startActivity(intent)
                openPdfFile(file)
            }
            builder.setNegativeButton("Không") { dialog, which ->
                dialog.dismiss() // Đóng AlertDialog khi nhấn nút "Không"
            }
            alertDialog = builder.create()
        }
        alertDialog?.show()

    }
    private fun openPdfFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            file
        )
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
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

    private fun setTrangThaiString(trangThai: Boolean): String {
        return if (trangThai) "Đã hoàn thành" else "Chưa hoàn thành"
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
//                    congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang+1, binding.etYear.text.toString().toInt())
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
    private fun openLichDialog1() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.tvDateStart.setText(dinhDangNgay(day, month, year))
                ngay = day
                thang2 = month
                nam = year
            }, nam, thang2, ngay
        )
        dialog.show()
    }

    private fun openLichDialog2() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.tvDateEnd.setText(dinhDangNgay(day, month, year))
                ngay = day
                thang2 = month
                nam = year
            }, nam, thang2, ngay
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


