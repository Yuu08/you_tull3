package com.example.you_tulle.ui.slideshow

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.you_tulle.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_slideshow.*
import java.text.SimpleDateFormat
import java.util.*

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it

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

                ExpenseList(gmail,date1,date2) {
                    //表示するFrameLayoutの高さを要素この数で動的に変更する
                    val param = layout.getLayoutParams()
                    param.height = 150+100*(it.size)
                    //表示する項目数をここで宣言
                    val items = arrayOf("date","genre", "category", "amount", "content")
                    //[件数][一件分の内容数]という2次元配列を用意する
                    val expensedata = Array(it.size, {arrayOfNulls<TextView?>(items.size)})
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
                            expensedata[i][j] = TextView(this.activity)
                            //Log.d("[items[j]]は表示する項目で処理を分けるために初期化したもの", items[j])
                            //dateはcom.google.firebase.Timestamp型なのでString型に変換する。
                            if(items[j].equals("date")){
                                var yearDate=TimestampToYear(it[i][items[j]] as com.google.firebase.Timestamp)
                                var monthDate=TimestampToMonth(it[i][items[j]] as com.google.firebase.Timestamp)
                                var dateDate=TimestampToDate(it[i][items[j]] as com.google.firebase.Timestamp)
                                expensedata[i][j]?.text =yearDate.toString()+"/"+monthDate.toString()+"/"+dateDate.toString()
                                //amountは数値型なのでtoStingで文字列型へ
                            }else if (items[j].equals("amount")){
                                expensedata[i][j]?.text = it[i][items[j]].toString()
                            }else{
                                expensedata[i][j]?.text = it[i][items[j]] as CharSequence?
                            }
                            expensedata[i][j]?.setTextColor(Color.BLACK)
                            //一覧の表示場所を設定
                            //1列目
                            expensedata[i][j]?.x = 50.0f* (j*3+ 1)
                            expensedata[i][j]?.y = 80.0f * i
                            //2列目
                            if(j>=3){
                                expensedata[i][j]?.x = 50.0f* ((j-2)*3+ 1)
                                expensedata[i][j]?.y = 80.0f * i +40.0f
                            }
                            layout.addView(
                                expensedata[i][j],
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


            //追加ボタンの処理-----------------------------------------------------------------------------------------------
            // idがdialogButtonのButtonを取得
            val dialogBtn: Button = root.findViewById(R.id.dialogButton) as Button
            // clickイベント追加
            // clickイベント追加

            dialogBtn.setOnClickListener{
                // クリックしたらダイアログを表示する処理

                // ダイアログクラスをインスタンス化
                val dialog = CustomDialogFlagment()
                // 表示  getFagmentManager()は固定、sampleは識別タグ

                this.activity?.supportFragmentManager?.let { it1 -> dialog.show(it1,"sss") }
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
    fun ExpenseList(gmail:String,date1:Date,date2:Date, myCallback: (MutableList<MutableMap<String,Any>>) -> Unit) {
        //協定世界時のUTC 1970年1月1日深夜零時との差をミリ秒で取得
        val millis = System.currentTimeMillis()
        //初期化
        var maptest :MutableMap<String,Any> = mutableMapOf<String, Any>("amount" to 1000,"category" to "カテゴリ", "content" to "内容", "date" to millis, "genre" to "ジャンル", "id" to "メールアドレス")
        //mutablemapを入れる配列　複数のmapを配列で管理
        var arraytest = mutableListOf(maptest)/////ここで配列を宣言している

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("expense")
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

    //追加ボタン--------------------------------------------------------------------------

    class CustomDialogFlagment : DialogFragment() {
        //DialogFragmentの画面サイズ
        override fun onStart() {
            super.onStart()
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.97).toInt()
            dialog?.window?.setLayout(width, height)
        }

        // ダイアログが生成された時に呼ばれるメソッド ※必須
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // ダイアログ生成  AlertDialogのBuilderクラスを指定してインスタンス化します
            val dialogBuilder = AlertDialog.Builder(activity)
            // タイトル設定
            dialogBuilder.setTitle("支出入力")
//            // 表示する文章設定
//            dialogBuilder.setMessage("入力はこちら" )

            val factory = LayoutInflater.from(this.activity)
            //dialogの画面をpopup.xmlで作成
            val textEntryView: View = factory.inflate(R.layout.popup, null)

            dialogBuilder.setView(textEntryView)

            SelectDate(textEntryView)

            //カテゴリのspinner
            readCategory() {

                //ジャンルを入れる変数
                var genreArray: ArrayList<String> =  arrayListOf()
                //配列では追加ができないからArrayListを使用した
                var spinnercontent:ArrayList<String> =  arrayListOf()//ゲーム
                var spinnercontent2:ArrayList<String> =  arrayListOf()//音楽
                var spinnercontent3:ArrayList<String> =  arrayListOf()//カスタマイズ
                val GoogleId = "123@gmail.com"
                //カスタマイズテーブルからカスタマイズした情報を取得する
                CustomizationCategory(GoogleId) {
                    //今回は検索結果が一件しか取得できない一つのメールアドレスに一つだけ
                    val fielddata: MutableList<MutableMap<String, Any>> = it[1] as MutableList<MutableMap<String, Any>>
                    Log.d("カスタマイズカテゴリ全体", fielddata.toString())
                    //カスタマイズをしていない場合何も取得できないので未設定であることを伝える。
                    //データを取ってこれないことによるエラーをトリガーに分岐処理を記述した。
                    try {
                        val fielddata1 = fielddata[1]
                        //firestoreに入っている配列はlistで返ってくるのでList<String>にキャストする。
                        val setcategorybox = fielddata1["category"] as ArrayList<String>
                        Log.d("カスタマイズカテゴリ一覧", setcategorybox.toString())
                        //カスタマイズしたカテゴリを代入する
                        spinnercontent3=setcategorybox
                    } catch(e: IndexOutOfBoundsException){
                        //カスタマイズの設定をしていなかったら設定していないことを伝える
                        spinnercontent3.add("カスタマイズ未設定")
                    }
                }

                var spinnerArrayList: ArrayList<ArrayList<String>> =  arrayListOf(spinnercontent,spinnercontent2,spinnercontent3)
                //選択されたジャンルのカテゴリを入れる配列
                var Selectgenre:ArrayList<String> =  spinnercontent// 選択したジャンル
                var i=1
                while(i<it.size) {
                    val itembox = it[i]
                    Log.d("一件分", itembox.toString())
                    Log.d("itembox[]", itembox["genre"].toString())
                    genreArray.add(itembox["genre"].toString())
                    //Log.d("ジャンル", genretext.toString())
                    //firestoreに入っている配列はlistで返ってくるのでList<String>にキャストする。
                    val categorybox = itembox["category"] as List<String>
                    var j :Int=0
                    while(j<categorybox.size) {
                        spinnerArrayList[i-1].add(categorybox[j])
                        j++
                    }

                    i++
                }
                //一番下にカスタマイズの項目を追加した
                genreArray.add("カスタマイズ")

                //spinnerの処理はfirebaseから受け取った時にしないといけなく、非同期がだめなので受け取った処理の中で書いた。
                //genreのspinner
                val spinner_genre: Spinner =
                    textEntryView.findViewById(R.id.genre_spinner)
                //ArrrayAdapterがエラーになった。　letで書いたらエラーが直った
                val adapter_genre =
                    activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, genreArray) }

                if (adapter_genre != null) {
                    adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                // spinner に adapter をセット
                spinner_genre.setAdapter(adapter_genre)

                Log.d("spinnergenre",spinner_genre.toString())
                // リスナーを登録
                spinner_genre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    //　アイテムが選択された時
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?, position: Int, id: Long
                    ) {
                        val spinner_ge = parent as Spinner
                        val item_ge = spinner_ge.selectedItem as String
                        //選択したジャンルによってカテゴリを変更する

                        Log.d("音楽",spinnercontent.toString())
                        Log.d("ゲーム",spinnercontent2.toString())
                        Log.d("カスタマイズ",spinnercontent3.toString())
                        if (Objects.equals(item_ge, "ゲーム")) {
                            Selectgenre=spinnercontent
                        }else if(Objects.equals(item_ge, "音楽")){
                            Selectgenre=spinnercontent2
                        }else if(Objects.equals(item_ge, "カスタマイズ")){
                            Selectgenre=spinnercontent3
                        }
                        //ジャンルが押されたときにカテゴリを変更するためにジャンルの中に記述
                        //categoryのspinner
                        val spinner_category: Spinner =
                            textEntryView.findViewById(R.id.category_spinner)
                        //ArrrayAdapterがエラーになった。　letで書いたらエラーが直った
                        val adapter_category =
                            activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, Selectgenre) }

                        if (adapter_category != null) {
                            adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }

                        // spinner に adapter をセット
                        spinner_category.setAdapter(adapter_category)

                        // リスナーを登録
                        spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            //　アイテムが選択された時
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?, position: Int, id: Long
                            ) {
                                val spinner_ca = parent as Spinner
                                val item_ca = spinner_ca.selectedItem as String
                                //特に処理はない

                            }

                            //　アイテムが選択されなかった
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                //
                            }
                        }

                    }
                    //　アイテムが選択されなかった
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //
                    }
                }


            }



            // OKボタン作成
            dialogBuilder.setPositiveButton(
                "OK"
            ) { dialog, which ->
                //OKを押したらfirebaseに登録する。
                //string型からdate型にした日付を取得
                val insertdate:Date=GetDate(textEntryView)
                val insertamount:Int=GetAmount(textEntryView)
                val insertgenre:String=Getgenre(textEntryView)
                val insertcategory:String=Getcategory(textEntryView)
                val insertContent:String=GetContent(textEntryView)
                val GoogleId = "tests@gmail.com"
                InsertExpense(insertdate,insertgenre,insertcategory,insertamount,insertContent,GoogleId)

                AlertDialog.Builder(context)
                    .setTitle("追加されました。")
                    .setPositiveButton("OK", { dialog, which ->
                        // TODO:Yesが押された時の挙動
                    })
                    .show()
            }

            // NGボタン作成
            dialogBuilder.setNegativeButton(
                "NG"
            ) { dialog, which ->
                // 何もしないで閉じる
            }

            // dialogBulderを返す
            val alertDialog: AlertDialog = dialogBuilder.create()

            return alertDialog
        }


        //popupの日付選択
        fun SelectDate(textEntryView:View) {
            val datebtn:Button = textEntryView.findViewById(R.id.inputdatebtn)
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var date = calendar.get(Calendar.DATE)
            datebtn.text = "${year}/${month + 1}/${date}"
            datebtn.setOnClickListener {
                //ロケールによる言語設定　もともとは英語になっていたので日本語化
                val locale = Locale("JPN")
                Locale.setDefault(locale)
                val calendar = Calendar.getInstance()
                var year = calendar.get(Calendar.YEAR)
                var month = calendar.get(Calendar.MONTH)
                var date = calendar.get(Calendar.DATE)
                val datePickerDialog = DatePickerDialog(
                    textEntryView.context,
                    DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
                        datebtn.text = "${year}/${month + 1}/${dayOfMonth}"
                    },
                    //初期位置を現在の日付にした。
                    year,
                    month,
                    date
                )
                datePickerDialog.show()
            }
        }

        //popup形式の内容をexpenseに追加
        fun InsertExpense(insertdate:Date,insertgenre:String,insertcategory:String,insertamount:Int,insertcontent:String,GoogleId:String):Unit{
            val TAG="支出登録"

            //フィールド名
            val Item = hashMapOf(
                "amount" to insertamount,
                "category" to insertcategory,
                "content" to insertcontent,
                "date" to insertdate,
                "genere" to insertgenre,
                "id" to GoogleId
            )
            val db = FirebaseFirestore.getInstance()
            db.collection("expense")   //"コレクション名になる
                // .document("ドキュメント名")でドキュメントを自分で設定することができる        ドキュメントを自分で設定した場合、.appではなく.setに変更する
                .add(Item)//フィールド名
                .addOnSuccessListener { documentReference -> //タスクが正常に完了したときに呼び出されるリスナー。
                    Log.d(TAG, "支出登録に成功しました。")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "支出登録に失敗しました。", e)
                }
        }


        //dateの取得
        fun GetDate(textEntryView:View) :Date{
            //dateが入っているボタン
            val datebtn = textEntryView.findViewById(R.id.inputdatebtn)as TextView
            //dateのテキストを取り出し
            val inputdate_string =datebtn.text as String
            //String型のdateをdate型に変換する関数へ渡す
            var date = StringToDate(inputdate_string)

            return date
        }
        //String型("yyyy/MM/dd")の日付をdate型で返す関数
        fun StringToDate(Stringdate: String):Date {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val str = Stringdate
            val date:Date = sdf.parse(str);
            return date
        }

        // ジャンルの取得
        fun Getgenre(textEntryView:View) :String{
            //ジャンルが入っているspinner
            val genrespinner = textEntryView.findViewById(R.id.genre_spinner)as Spinner
            //ジャンルのテキストを取り出し
            val inputgenretext = genrespinner.getSelectedItem().toString()
            return inputgenretext
        }
        // カテゴリの取得
        fun Getcategory(textEntryView:View) :String{
            //カテゴリが入っているspinner
            val categoryspinner = textEntryView.findViewById(R.id.category_spinner)as Spinner
            //ジャンルのテキストを取り出し
            val inputcategorytext = categoryspinner.getSelectedItem().toString()
            return inputcategorytext
        }
        // 金額の取得
        fun GetAmount(textEntryView:View) :Int{
            //金額が入っているedittext
            val amounttext = textEntryView.findViewById(R.id.amounttext)as EditText
            //金額のテキストを取り出し
            val inputamount_editable =amounttext.text
            val inputamount_string=inputamount_editable.toString()
            val inputamount_int=inputamount_string.toInt()
            return inputamount_int
        }
        //内容の取得
        fun GetContent(textEntryView:View) :String{
            //内容が入っているedittext
            val contenttext = textEntryView.findViewById(R.id.contenttext)as EditText
            //内容のテキストを取り出し
            val inputcontent_editable =contenttext.text
            val inputcontent_string=inputcontent_editable.toString()
            return inputcontent_string
        }


        fun readCategory(myCallback: (MutableList<MutableMap<String,Any>>) -> Unit) {

            var maptest :MutableMap<String,Any> = mutableMapOf<String, Any>("category" to "カテゴリ", "genre" to "ジャンル")
            //mutablemapを入れる配列　複数のmapを配列で管理
            var arraytest = mutableListOf(maptest)/////ここで配列を宣言している

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("expense_category")
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


        fun CustomizationCategory(gmail:String,myCallback: (ArrayList<Any?>) -> Unit) {
//        fun readSetCategory(gmail:String,myCallback: (MutableList<MutableMap<String,Any>>) -> Unit) {

            var maptest :MutableMap<String,Any> = mutableMapOf<String, Any>("category" to "カテゴリ", "genre" to "ジャンル")
            //mutablemapを入れる配列　複数のmapを配列で管理
            var document_id_list= arrayListOf<String>()
            var mutableList = mutableMapOf<String,Any>()
            var arraytest = mutableListOf(maptest)/////ここで配列を宣言している
            var databox= arrayListOf<Any?>()
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("expense_customized")
                .whereEqualTo("id", gmail)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            mutableList = document.data
                            var documentid = document.id
                            document_id_list.add(documentid)
                            arraytest.add(mutableList)
                        }
                        databox.add(document_id_list )
                        databox.add(arraytest)
                        myCallback(databox)
                    }
                }
        }

    }



}