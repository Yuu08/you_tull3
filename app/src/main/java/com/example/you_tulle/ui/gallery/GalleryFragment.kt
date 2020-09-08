package com.example.you_tulle.ui.gallery

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.you_tulle.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.text.SimpleDateFormat
import java.util.*

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
//        val textView: TextView = root.findViewById(R.id.text_gallery)    //コメントアウトしておいた


        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it//コメントアウトしておいた

//firebaseの処理
            val gmail = "123@gmail.com"

            button.setOnClickListener {
                //表示する内容のリセット
                val layout: FrameLayout = root.findViewById(R.id.textsWindow)
                layout.removeAllViews();
                //選択した開始日と終了日を取得
                val Arraydate=BeginEnddate()//選択した終了日と開始日を取得
                val date1=Arraydate[0]//開始日
                val date2=Arraydate[1]//終了日

                IncomeList(gmail,date1,date2) {

                    //表示するFrameLayoutの高さを要素この数で動的に変更する
                    val param = layout.getLayoutParams()
                    param.height = 150+70*(it.size)
                    //表示する項目数をここで宣言
                    val items = arrayOf("date","genre", "category", "amount", "content")
                    //[件数][一件分の内容数]という2次元配列を用意する
                    val Incomedata = Array(it.size, {arrayOfNulls<TextView?>(items.size)})
                    //i[0]には初期化した内容が入っているので、iは１から
                    //Log.d("初期化", it[0].toString())
                    //検索結果がなしの処理
                    if(it.size==1){
                        val layout: FrameLayout = root.findViewById(R.id.textsWindow)
                        val nodata = TextView(this.activity)
                        nodata?.setTextColor(Color.BLACK)
                        nodata?.x = 100.0f
                        nodata?.y = 80.0f
                        nodata?.text = "検索結果なし"
                        layout.addView(
                            nodata,
                            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50)
                        )
                    }
                    var i=1
                    while(i<it.size){
                        //it[i]は一件分のデータ
                        //Log.d("TAG", it[i].toString())
                        // FrameLayout のインスタンスを取得しています
                        // textsWindow は予めレイアウトファイル(.xml)に定義したものです
                        val layout: FrameLayout = root.findViewById(R.id.textsWindow)
                        // それぞれのTextView に設定を行い、FrameLayout のインスタンスに追加しています
                        // 今回はそれぞれのテキストが重なってもOKなように、FrameLayout を使っています
                        // 特に制約がなければ、LinearLayout などでも構いません
                        var j:Int = 0
                        while(j<items.size) {
                            Incomedata[i][j] = TextView(this.activity)
                            //Log.d("[items[j]]は表示する項目で処理を分けるために初期化したもの", items[j])
                            //dateはcom.google.firebase.Timestamp型なのでString型に変換する。
                            if(items[j].equals("date")){
                                var yearDate=TimestampToYear(it[i][items[j]] as com.google.firebase.Timestamp)
                                var monthDate=TimestampToMonth(it[i][items[j]] as com.google.firebase.Timestamp)
                                var dateDate=TimestampToDate(it[i][items[j]] as com.google.firebase.Timestamp)
                                Incomedata[i][j]?.text =yearDate.toString()+"/"+monthDate.toString()+"/"+dateDate.toString()
                                //amountは数値型なのでtoStingで文字列型へ
                            }else if (items[j].equals("amount")){
                                Incomedata[i][j]?.text = it[i][items[j]].toString()
                            }else{
                                Incomedata[i][j]?.text = it[i][items[j]] as CharSequence?
                            }
                            Incomedata[i][j]?.setTextColor(Color.BLACK)
                            //一覧の表示場所を設定
                            //1列目
                            Incomedata[i][j]?.x = 50.0f* (j*3+ 1)
                            Incomedata[i][j]?.y = 80.0f * i
                            //2列目
                            if(j>=3){
                                Incomedata[i][j]?.x = 50.0f* ((j-2)*3+ 1)
                                Incomedata[i][j]?.y = 80.0f * i +40.0f
                            }
                            layout.addView(
                                Incomedata[i][j],
                                LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50)
                            )
                            j++
                        }
                        i++
                    }
                }
            }

            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var date = calendar.get(Calendar.DATE)
            EndDate.text = "${year}/${month + 1}/${date}"
            //日付を選択するボタンの実装
            BeginDate.setOnClickListener {
                showDatePicker_begin()
            }
            EndDate.setOnClickListener {
                showDatePicker_end()

            }

//---------------------------------------------firebaseの処理ここまで
        })

        return root
    }

    //関数一覧
    //開始日付と終了日をdate型で取得する関数　配列で返す　0=開始日　1=終了日
    fun BeginEnddate():Array<Date>{
        //dateが入っているボタン
        val begindatetext = getActivity()?.findViewById(R.id.BeginDate) as TextView
        val enddatetext = getActivity()?.findViewById(R.id.EndDate) as TextView
        //dateのテキストを取り出し
        val begindate_string =begindatetext.text as String
        val enddate_string =enddatetext.text as String
        //String型のdateをdate型に変換する関数へ渡す
        var begindate = StringToDate(begindate_string)
        var enddate = StringToDate(enddate_string)
        //開始日付と終了日付の順番を整える
        if(begindate.after(enddate)){
            val tmpdate:Date
            tmpdate = begindate
            begindate = enddate
            enddate = tmpdate
        }

        //配列にして返す。　二つのデータを返すために
        val dateArray = arrayOf(begindate,enddate)
        return dateArray
    }
    //日付で絞り込みをするための処理(関数一覧)
    //String型("yyyy/MM/dd")の日付をdate型で返す関数
    fun StringToDate(Stringdate: String):Date {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val str = Stringdate
        val date:Date = sdf.parse(str);
        return date
    }
    fun IncomeList(gmail:String,date1:Date,date2:Date, myCallback: (MutableList<MutableMap<String,Any>>) -> Unit) {
        //協定世界時のUTC 1970年1月1日深夜零時との差をミリ秒で取得
        val millis = System.currentTimeMillis()
        //初期化
        var maptest :MutableMap<String,Any> = mutableMapOf<String, Any>("amount" to 1000,"category" to "カテゴリ", "content" to "内容", "date" to millis, "genre" to "ジャンル", "id" to "メールアドレス")
        //mutablemapを入れる配列　複数のmapを配列で管理
        var arraytest = mutableListOf(maptest)/////ここで配列を宣言している

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("income")
            .whereEqualTo("id", gmail)
            .whereLessThan("date",  date2)
            .whereGreaterThan("date", date1)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val mutableList = document.data
                        arraytest.add(mutableList)
                    }
                    myCallback(arraytest)
                }
            }
    }
    //com.google.firebase.Timestamp型のデータを渡して年を返す
    fun TimestampToYear(TimeDate: com.google.firebase.Timestamp):Int{
        //timestamp型をdate型へ
        val date = TimeDate.toDate()
        //calendarクラスのオブジェクトの生成
        val calendar = Calendar.getInstance()
        //Date型をいれる　
        calendar.time = date
        //CalendarやDateでは月を0から始まる数で扱うため、1月＝'0'、2月＝'1'、・・・12月＝"11"、となります。
        var year = calendar.get(Calendar.YEAR);
        return year
    }
    //com.google.firebase.Timestamp型のデータを渡して月を返す
    fun TimestampToMonth(TimeDate: com.google.firebase.Timestamp):Int{
        //timestamp型をdate型へ
        val date = TimeDate.toDate()
        //calendarクラスのオブジェクトの生成
        val calendar = Calendar.getInstance()
        //Date型をいれる　
        calendar.time = date
        //CalendarやDateでは月を0から始まる数で扱うため、1月＝'0'、2月＝'1'、・・・12月＝"11"、となります。
        var month = calendar.get(Calendar.MONTH) + 1;
        return month
    }
    //com.google.firebase.Timestamp型のデータを渡して日を返す
    fun TimestampToDate(TimeDate: com.google.firebase.Timestamp):Int{
        //timestamp型をdate型へ
        val date = TimeDate.toDate()
        //calendarクラスのオブジェクトの生成
        val calendar = Calendar.getInstance()
        //Date型をいれる　
        calendar.time = date
        //CalendarやDateでは月を0から始まる数で扱うため、1月＝'0'、2月＝'1'、・・・12月＝"11"、となります。
        var Date = calendar.get(Calendar.DATE) + 1
        return Date
    }


    //選択した日付を表示する関数(開始日)
    fun showDatePicker_begin() {
        //ロケールによる言語設定　もともとは英語になっていたので日本語化
        val locale = Locale("JPN")
        Locale.setDefault(locale)
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var date = calendar.get(Calendar.DATE)
        val datePickerDialog = DatePickerDialog(
            this.activity,
            DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth->
                BeginDate.text = "${year}/${month + 1}/${dayOfMonth}"
            },
            //初期位置を現在の日付にした。
            year,
            month,
            date)
        datePickerDialog.show()
    }
    //選択した日付を表示する関数(終了日)
    fun showDatePicker_end() {
        //ロケールによる言語設定　もともとは英語になっていたので日本語化
        val locale = Locale("JPN")
        Locale.setDefault(locale)
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var date = calendar.get(Calendar.DATE)
        val datePickerDialog = DatePickerDialog(
            this.activity,
            DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth->
                EndDate.text = "${year}/${month + 1}/${dayOfMonth}"
            },
            //初期位置を現在の日付にした。
            year,
            month,
            date)
        datePickerDialog.show()
    }
}