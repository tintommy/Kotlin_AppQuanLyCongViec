package com.example.kotlin_appquanlycongviec.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TabHost
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentExportEventBinding
import com.example.kotlin_appquanlycongviec.databinding.LayoutExportEventByDateBinding
import com.example.kotlin_appquanlycongviec.databinding.LayoutExportEventByMonthBinding
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.SuKienViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ExportEventFragment : Fragment() {
    private lateinit var binding: FragmentExportEventBinding
    private lateinit var bindingExportByDate: LayoutExportEventByDateBinding
    private lateinit var bindingExportByMonth: LayoutExportEventByMonthBinding
    private val suKienViewModel by viewModels<SuKienViewModel>()
    private lateinit var tableContent: Array<Array<String>>
    private val REQUEST_CODE = 1232
    private lateinit var pdfDocument: PdfDocument
    private lateinit var pageInfo: PageInfo
    private lateinit var page: PdfDocument.Page
    private var pageNumber = 0
    private lateinit var canvas: Canvas
    // Khai báo biến toàn cục
    private var alertDialog: AlertDialog? = null
    private val recordsNotYetPrint = arrayOf("", "", "", "", "")
    private val totalTableWidth = 1000
    private var todayList: MutableList<SuKien> = mutableListOf()
    private var nearlyList: MutableList<SuKien> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExportEventBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val years = arrayOf("2020", "2021", "2022", "2023", "2024", "2025")
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        val yearAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        val monthAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinnerYear = view.findViewById<Spinner>(R.id.spinnerYear)
        val spinnerMonth = view.findViewById<Spinner>(R.id.spinnerMonth)

        spinnerYear.adapter = yearAdapter
        spinnerMonth.adapter = monthAdapter
        // Lấy năm hiện tại
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        // Lấy tháng hiện tại (tháng trong Java bắt đầu từ 0 nên cần cộng thêm 1)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        // Set năm hiện tại cho spinnerYear
        val yearPosition = years.indexOf(currentYear.toString())
        if (yearPosition != -1) {
            spinnerYear.setSelection(yearPosition)
        }

        // Set tháng hiện tại cho spinnerMonth
        val monthPosition = months.indexOf(currentMonth.toString())
        if (monthPosition != -1) {
            spinnerMonth.setSelection(monthPosition)
        }

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedYear = parent.getItemAtPosition(position).toString()
                // Handle selected year
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle when nothing is selected
            }
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedMonth = parent.getItemAtPosition(position).toString()
                // Handle selected month
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle when nothing is selected
            }
        }
        // Khởi tạo bindingExportByDate
        bindingExportByDate = LayoutExportEventByDateBinding.bind(view)
        val tabHost = binding.tabHost
        tabHost.setup()

        // Tạo tab thứ nhất
        var spec: TabHost.TabSpec = tabHost.newTabSpec("Tab One")
        spec.setContent(R.id.tab1)
        spec.setIndicator("Theo Ngày")
        tabHost.addTab(spec)

        // Tạo tab thứ hai
        spec = tabHost.newTabSpec("Tab Two")
        spec.setContent(R.id.tab2)
        spec.setIndicator("Theo Tháng")
        tabHost.addTab(spec)
        // Thiết lập OnClickListener cho startDateInput
        bindingExportByDate.startDateInput.setOnClickListener {
            askPermission()
            // Tạo một DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Cập nhật text của startDateInput khi ngày được chọn
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    bindingExportByDate.startDateInput.setText(selectedDate)
                },
                Calendar.getInstance()[Calendar.YEAR],
                Calendar.getInstance()[Calendar.MONTH],
                Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            )

            // Hiển thị DatePickerDialog
            datePickerDialog.show()
        }
        bindingExportByDate.endDateInput.setOnClickListener() {
            // Tạo một DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Cập nhật text của startDateInput khi ngày được chọn
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    bindingExportByDate.endDateInput.setText(selectedDate)
                },
                Calendar.getInstance()[Calendar.YEAR],
                Calendar.getInstance()[Calendar.MONTH],
                Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            )

            // Hiển thị DatePickerDialog
            datePickerDialog.show()
        }
        bindingExportByDate.btnExportByDate.setOnClickListener() {
            // Kiểm tra xem ngày bắt đầu có được chọn hay không
            if (bindingExportByDate.startDateInput.text?.isEmpty() == true) {
                Toast.makeText(context, "Vui lòng chọn ngày bắt đầu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Kiểm tra xem ngày kết thúc có được chọn hay không
            if (bindingExportByDate.endDateInput.text?.isEmpty() == true) {
                Toast.makeText(context, "Vui lòng chọn ngày kết thúc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

// Chuyển đổi chuỗi ngày thành đối tượng Date
            val startDateDate = format.parse(bindingExportByDate.startDateInput.text.toString())
            val endDateDate = format.parse(bindingExportByDate.endDateInput.text.toString())

// Lấy ngày, tháng, năm từ đối tượng Date
            val startDay =
                Calendar.getInstance().apply { time = startDateDate }.get(Calendar.DAY_OF_MONTH)
            val startMonth =
                Calendar.getInstance().apply { time = startDateDate }.get(Calendar.MONTH)
            val startYear = Calendar.getInstance().apply { time = startDateDate }.get(Calendar.YEAR)

            val endDay =
                Calendar.getInstance().apply { time = endDateDate }.get(Calendar.DAY_OF_MONTH)
            val endMonth = Calendar.getInstance().apply { time = endDateDate }.get(Calendar.MONTH)
            val endYear = Calendar.getInstance().apply { time = endDateDate }.get(Calendar.YEAR)

// Sử dụng ngày, tháng, năm để gọi hàm dinhDangNgayAPI
            val startDate = dinhDangNgayAPI(startDay, startMonth, startYear)
            val endDate = dinhDangNgayAPI(endDay, endMonth, endYear)


            // Kiểm tra xem ngày bắt đầu có sau ngày kết thúc hay không
            if (startDateDate.after(endDateDate)) {
                Toast.makeText(
                    context,
                    "Ngày bắt đầu không được sau ngày kết thúc",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            suKienViewModel.getEventFromDateToDate(startDate, endDate)
            lifecycleScope.launch {
                suKienViewModel.allEventFromDateToDate.collectLatest {
                    when (it) {
                        is Resource.Loading<*> -> {

                        }

                        is Resource.Success -> {
                            tableContent = Array(it.data?.size ?: 0) { Array(4) { "" } }
                            it.data?.let { data ->
                                for (i in data.indices) {
                                    tableContent[i][0] = data[i]?.tenSuKien ?: ""
                                    tableContent[i][1] = if (data[i]?.ngay != null) dinhDangNgay(data[i]?.ngay!!) else ""
//                                    tableContent[i][1] = data[i]?.ngay ?: ""
                                    tableContent[i][2] = data[i]?.gio ?: ""
                                    tableContent[i][3] = data[i]?.moTa ?: ""
                                }
                            }
                            tableContent.forEach { row ->
                                row.forEach { cell ->
                                    print("$cell \t")
                                }
                                println()
                            }
                            createPDF(startDate, endDate)
                        }

                        is Resource.Error -> {
                            if (it.message.equals("404")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Không có sự kiện nào trong khoảng thời gian này",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        else -> {}


                    }
                }
            }


        }
        // Initialize bindingExportByMonth
        bindingExportByMonth = LayoutExportEventByMonthBinding.bind(view)
        bindingExportByMonth.btnExportByMonth.setOnClickListener() {
            val selectedYear = spinnerYear.selectedItem.toString().toInt()
            val selectedMonth = spinnerMonth.selectedItem.toString().toInt()
            val startDate = dinhDangNgayAPI(1, selectedMonth.toInt() - 1, selectedYear.toInt())
            val endDate = dinhDangNgayAPI(
                getLastDayOfMonth(selectedYear, selectedMonth - 1),
                selectedMonth - 1,
                selectedYear
            )
            suKienViewModel.getEventFromDateToDate(startDate, endDate)
            lifecycleScope.launch {
                suKienViewModel.allEventFromDateToDate.collectLatest {


                    when (it) {
                        is Resource.Loading<*> -> {

                        }

                        is Resource.Success<*> -> {
                            tableContent = Array(it.data?.size ?: 0) { Array(4) { "" } }
                            it.data?.let { data ->
                                for (i in data.indices) {
                                    tableContent[i][0] = data[i]?.tenSuKien ?: ""
                                    tableContent[i][1] = data[i]?.ngay ?: ""
                                    tableContent[i][2] = data[i]?.gio ?: ""
                                    tableContent[i][3] = data[i]?.moTa ?: ""
                                }
                            }
                            tableContent.forEach { row ->
                                row.forEach { cell ->
                                    print("$cell \t")
                                }
                                println()
                            }
                            createPDF(startDate, endDate)
                        }

                        is Resource.Error -> {
                            if (it.message.equals("404")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Không có sự kiện nào trong khoảng thời gian này",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        else -> {}

                    }
                }
            }
        }

    }
    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }
    @SuppressLint("MissingInflatedId")
    private fun createPDF(startDate: String, endDate: String) {
        pageNumber++
        pdfDocument = PdfDocument()
        pageInfo = PageInfo.Builder(1080, 1920, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas
        val paint = Paint()
        paint.color = Color.BLUE
        paint.textSize = 42f
        val title = "THỐNG KÊ DANH SÁCH SỰ KIỆN"
        val timeRange = "Từ ngày "+dinhDangNgay(startDate) + " đến ngày "+dinhDangNgay(endDate)
        canvas.drawText(title, (1080 - paint.measureText(title)) / 2, 50f, paint)
        canvas.drawText(timeRange, (1080 - paint.measureText(timeRange)) / 2, 150f, paint)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        val column0Content = "STT"
        val column1Content = "TÊN SỰ KIỆN"
        val column2Content = "NGÀY"
        val column3Content = "THỜI GIAN"
        val column4Content = "MÔ TẢ"
        val xOfCardinalNumber = 50
        val xOfEventNameColumn = 120
        val xOfDate = 360
        val xOfTime = 540
        val xOfDescription = 730
        val yOfHeader = 200
        val heightfHeader = 90
        val widthOfCardinalNumber = 60
        val widthOfEventName = 230
        val widthOfDate = 170
        val widthOfTime = 180
        val widthOfDescription = 320
        val textBounds = Rect()
        paint.color = resources.getColor(R.color.header_background)
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            xOfCardinalNumber.toFloat(),
            yOfHeader.toFloat(),
            (xOfCardinalNumber + widthOfCardinalNumber).toFloat(),
            (yOfHeader + heightfHeader).toFloat(),
            paint
        )
        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.getTextBounds(column0Content, 0, column0Content.length, textBounds)
        val textHeightOfCardinalNumber = textBounds.height()
        canvas.drawText(
            column0Content,
            xOfCardinalNumber + (50 - paint.measureText(column0Content)) / 2,
            (yOfHeader + heightfHeader.toFloat() / 2 + textHeightOfCardinalNumber / 2).toFloat(),
            paint
        )
        paint.color = resources.getColor(R.color.header_background)
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            xOfEventNameColumn.toFloat(),
            yOfHeader.toFloat(),
            (widthOfEventName + xOfEventNameColumn).toFloat(),
            (yOfHeader + heightfHeader).toFloat(),
            paint
        )
        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.getTextBounds(column1Content, 0, column1Content.length, textBounds)
        val textHeightOfEventName = textBounds.height()
        canvas.drawText(
            column1Content,
            xOfEventNameColumn + (widthOfEventName - paint.measureText(column1Content)) / 2,
            (yOfHeader + heightfHeader.toFloat() / 2 + textHeightOfEventName / 2).toFloat(),
            paint
        )
        paint.color = resources.getColor(R.color.header_background)
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            xOfDate.toFloat(),
            yOfHeader.toFloat(),
            (widthOfDate + xOfDate).toFloat(),
            (yOfHeader + heightfHeader).toFloat(),
            paint
        )
        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.getTextBounds(column2Content, 0, column2Content.length, textBounds)
        val textHeightOfDate = textBounds.height()
        canvas.drawText(
            "NGÀY",
            xOfDate + (widthOfDate - paint.measureText(column2Content)) / 2,
            (yOfHeader + heightfHeader.toFloat() / 2 + textHeightOfDate / 2).toFloat(),
            paint
        )
        paint.color = resources.getColor(R.color.header_background)
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            xOfTime.toFloat(),
            yOfHeader.toFloat(),
            (widthOfTime + xOfTime).toFloat(),
            (yOfHeader + heightfHeader).toFloat(),
            paint
        )
        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.getTextBounds(column3Content, 0, column3Content.length, textBounds)
        val textHeightOfTime = textBounds.height()
        canvas.drawText(
            "THỜI GIAN",
            xOfTime + (widthOfTime - paint.measureText(column3Content)) / 2,
            (yOfHeader + heightfHeader.toFloat() / 2 + textHeightOfTime / 2).toFloat(),
            paint
        )
        paint.color = resources.getColor(R.color.header_background)
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            xOfDescription.toFloat(),
            yOfHeader.toFloat(),
            (widthOfDescription + xOfDescription).toFloat(),
            (yOfHeader + heightfHeader).toFloat(),
            paint
        )
        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.getTextBounds(column4Content, 0, column4Content.length, textBounds)
        val textHeightOfDescription = textBounds.height()
        canvas.drawText(
            "MÔ TẢ",
            xOfDescription + (widthOfDescription - paint.measureText(column4Content)) / 2,
            (yOfHeader + heightfHeader.toFloat() / 2 + textHeightOfDescription / 2).toFloat(),
            paint
        )
        paint.color = Color.BLACK
        paint.textSize = 30f
        val xOfTable = 50
        val yOfTable = 280
        val yOfTableInNextPage = 20
        val columnWidth = intArrayOf(70, 230, 210, 170, 270)
        val rowHeights = intArrayOf(0, 0, 0, 0, 0)
        val rowHeight = 60
        val numRows = tableContent.size
        val numColumns = columnWidth.size


        // Draw table records


        // Draw table records
        var yPrintText = yOfTable

        for (row in 0 until numRows) {
            var xPrintText = xOfTable
            yPrintText += if (getMaxRowHeight(rowHeights) > 30) getMaxRowHeight(rowHeights) + 30 else rowHeight
            if (yPrintText > 1870) {
                createNextPage()
                yPrintText = yOfTableInNextPage
                for (i in 0 until numColumns) {
                    if (i == 0) {
//                        Not print cardinal number in the first column of first row of the next page
//                        rowHeights[i] = drawTextWithWrap(canvas, paint, String.valueOf(row+1), xPrintText, yPrintText, columnWidth[i], yOfTableInNextPage, pageNumber, i);
                    } else {
                        rowHeights[i] = drawTextWithWrap(
                            canvas, paint,
                            recordsNotYetPrint[i], xPrintText.toFloat(), yPrintText.toFloat(),
                            columnWidth[i], yOfTableInNextPage, pageNumber, i, row
                        )
                    }
                    xPrintText += columnWidth[i] + 5
                }
                yPrintText += getMaxRowHeight(rowHeights) + 30
                xPrintText = xOfTable
            }
            for (i in 0 until numColumns) {
                if (i == 0) {
                    rowHeights[i] = drawTextWithWrap(
                        canvas,
                        paint,
                        (row + 1).toString(),
                        xPrintText.toFloat(),
                        yPrintText.toFloat(),
                        columnWidth[i],
                        yOfTableInNextPage,
                        pageNumber,
                        i,
                        row
                    )
                } else {
                    rowHeights[i] = drawTextWithWrap(
                        canvas, paint,
                        tableContent[row][i - 1], xPrintText.toFloat(), yPrintText.toFloat(),
                        columnWidth[i], yOfTableInNextPage, pageNumber, i, row
                    )
                }
                xPrintText += columnWidth[i] + 5
            }
        }

        pdfDocument.finishPage(page)

        val pdfFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val sdf = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault())
        val currentTime = Date()
        val formattedTime = sdf.format(currentTime)
        val fileName = "danh sach su kien-$formattedTime.pdf"

        val file = File(pdfFile, fileName)
        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            Toast.makeText(requireContext(), "Lưu file $fileName thành công ở download ", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            Log.d("mylog", "Error while writing $e")
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            file
        )
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(uri, "application/pdf")
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        startActivity(intent)
        if(alertDialog == null) {
            var builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Xuất file thành công")
            builder.setMessage("Bạn có muốn xem file PDF vừa xuất không?")
            builder.setPositiveButton("Xem") { dialog, which ->
                val intent = Intent(Intent.ACTION_VIEW)
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".provider",
                    file
                )
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
                alertDialog = null
            }
            builder.setNegativeButton("Không") { dialog, which ->
                dialog.dismiss()
            }
            alertDialog = builder.create()
        }


        alertDialog?.show()







//        // Tạo một AlertDialog.Builder
//        val builder = AlertDialog.Builder(requireContext())
//
//// Thiết lập tiêu đề và thông điệp cho AlertDialog
//        builder.setTitle("Xuất file thành công")
//        builder.setMessage("Bạn có muốn xem file PDF vừa xuất không?")
//
//// Thêm nút "Xem" và xử lý sự kiện khi nút này được nhấn
//        builder.setPositiveButton("Xem") { dialog, which ->
//            val intent = Intent(Intent.ACTION_VIEW)
//            val uri = FileProvider.getUriForFile(
//                requireContext(),
//                requireContext().applicationContext.packageName + ".provider",
//                file
//            )
//            intent.setDataAndType(uri, "application/pdf")
//            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            startActivity(intent)
//        }
//
//// Thêm nút "Không" và xử lý sự kiện khi nút này được nhấn
//        builder.setNegativeButton("Không") { dialog, which ->
//            // Đóng dialog
//            dialog.dismiss()
//        }
//
//// Tạo và hiển thị AlertDialog
//        val dialog = builder.create()
//        dialog.show()













    }
    private fun getMaxRowHeight(rowHeights: IntArray): Int {
        var rowHeightMax = 0
        for (i in rowHeights.indices) {
            if (rowHeightMax < rowHeights[i]) {
                rowHeightMax = rowHeights[i]
            }
        }
        return rowHeightMax
    }
    private fun createNextPage() {
        pageNumber++
        pdfDocument.finishPage(page)
        pageInfo = PageInfo.Builder(1080, 1920, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas
    }
    private fun drawTextWithWrap(
        canvas: Canvas,
        paint: Paint,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Int,
        yOfTableInNextPage: Int,
        pageNumber: Int,
        column: Int,
        row: Int
    ): Int {
        var y = y
        var startIndex = 0
        var endIndex: Int
        val textLength = text.length
        var textHeight = 0
        val textBounds = Rect()
        while (startIndex < textLength) {
            endIndex = paint.breakText(text, startIndex, textLength, true, maxWidth.toFloat(), null)
            val substring = text.substring(startIndex, startIndex + endIndex)
            paint.getTextBounds(substring, 0, endIndex, textBounds)

            // Set the background color based on whether the row is even or odd
//            if (row % 2 == 0) {
//                paint.setColor(Color.LTGRAY); // Change this to the color you want for even rows
//            } else {
//                paint.setColor(Color.WHITE); // Change this to the color you want for odd rows
//            }
//
//            // Draw the background color
////            canvas.drawRect(xOfTable, yPrintText, xOfTable + totalTableWidth, yPrintText + rowHeight, paint);
//            canvas.drawRect(50, y - 30, 50 + totalTableWidth, y + 30, paint);
//
//            // Reset the paint color for the text
//            paint.setColor(Color.BLACK);
            canvas.drawText(text, startIndex, startIndex + endIndex, x, y, paint)
            textHeight += getTextHeight(paint, text)
            startIndex += endIndex
            y += paint.fontSpacing
            if (y > 1870) {
                recordsNotYetPrint[column] = text.substring(startIndex)
                break
            }
        }
        return textHeight
    }
    private fun getTextHeight(paint: Paint, text: String): Int {
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        return textBounds.height()
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
    fun dinhDangNgay(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
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
    fun getLastDayOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}