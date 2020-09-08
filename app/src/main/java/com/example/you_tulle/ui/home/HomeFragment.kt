package com.example.you_tulle.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.you_tulle.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            val gmail = "123@gmail.com"
            //月のspinner
            val spinnerItems_month = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
            // Spinnerの取得
            val spinner_month = root.findViewById<Spinner>(R.id.spinner_month)

            // Adapterの生成
            val adapter_month =
                ArrayAdapter(this.activity, android.R.layout.simple_spinner_item, spinnerItems_month)

            // AdapterをSpinnerのAdapterとして設定
            spinner_month.adapter = adapter_month
            //月を現在の月にデフォルトで選択した。
            val spinner_default = Calendar.getInstance()
            var month_default = spinner_default.get(Calendar.MONTH)

            spinner_month.setSelection(month_default)
            // リスナーを登録
            spinner_month.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                //　アイテムが選択された時
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val spinner_m = parent as Spinner
                    val item_m = spinner_m.selectedItem.toString()
                    //月が1～１２まで選択されたときに合計の関数を動かす。
                    var i=1
                    while(i<=12) {
                        if (Objects.equals(item_m, i.toString())) {
                            SumAmount(gmail,root)
                        }
                        i++
                    }
                }
                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //
                }
            }

            //年のspinner
            val spinner_calendar = Calendar.getInstance()
            var year_items = spinner_calendar.get(Calendar.YEAR)
            val spinnerItems_year = arrayOf(
                year_items,
                year_items - 1,
                year_items - 2,
                year_items - 3,
                year_items - 4,
                year_items - 5
            )
            // Spinnerの取得
            val spinner_year = root.findViewById<Spinner>(R.id.spinner_year)
            // Adapterの生成
            val adapter_year =
                ArrayAdapter(this.activity, android.R.layout.simple_spinner_item, spinnerItems_year)

            // AdapterをSpinnerのAdapterとして設定
            spinner_year.adapter = adapter_year

            // リスナーを登録
            spinner_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                //　アイテムが選択された時
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val spinner_y = parent as Spinner
                    val item_y = spinner_y.selectedItem.toString()

                    var i=0
                    while(i<6) {
                        if (Objects.equals(item_y, (year_items-i).toString())) {
                            SumAmount(gmail,root)
                        }
                        i++
                    }
                }

                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //
                }
            }
            //年単位の合計金額

            //年のspinner
            val spinner_calendar2 = Calendar.getInstance()
            var year_items2 = spinner_calendar2.get(Calendar.YEAR)
            val spinnerItems_year2 = arrayOf(
                year_items2,
                year_items2 - 1,
                year_items2 - 2,
                year_items2 - 3,
                year_items2 - 4,
                year_items2 - 5
            )
            // Spinnerの取得
            val spinner_year2 = root.findViewById<Spinner>(R.id.spinner_year2)
            // Adapterの生成
            val adapter_year2 =
                ArrayAdapter(this.activity, android.R.layout.simple_spinner_item, spinnerItems_year2)

            // AdapterをSpinnerのAdapterとして設定
            spinner_year2.adapter = adapter_year

            // リスナーを登録
            spinner_year2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                //　アイテムが選択された時
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val spinner_y = parent as Spinner
                    val item_y = spinner_y.selectedItem.toString()


                    var i=0
                    while(i<6) {
                        if (Objects.equals(item_y, (year_items-i).toString())) {
                            SumYear(gmail,root)
                        }
                        i++
                    }
                }

                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //
                }
            }


        })
        return root
    }


    fun SumAmount(gmail: String,root:View): Unit {
        // 年を取得
        val date_y = root.findViewById(R.id.spinner_year) as Spinner
        // 選択されているアイテムを取得
        val item_y_string = date_y.selectedItem.toString()
        val item_y = Integer.parseInt(item_y_string)
        // 月を取得
        val date_m = root.findViewById(R.id.spinner_month) as Spinner
        // 選択されているアイテムを取得
        val item_m_string = date_m.selectedItem.toString()
        val item_m = Integer.parseInt(item_m_string)
        val calendar = Calendar.getInstance()
        //              year      month     date 0にしないと検索するときの条件おかしくなる       time       minute     second
        calendar.set(item_y, item_m - 1, 0, 0, 0, 0);
        //calendar型からdate型に変換する
        val date1: Date = calendar.getTime()
        //１月～２月までと指定するために+１
        calendar.add(Calendar.MONTH, 1);
        val date2: Date = calendar.getTime()
        IncomeGetAmount(gmail, date1, date2) {
            //表示する項目数をここで宣言
            val items = arrayOf("date", "genre", "category", "amount", "content")
            //[件数][一件分の内容数]という2次元配列を用意する
            val Incomedata = Array(it.size, { arrayOfNulls<TextView?>(items.size) })
            //i[0]には初期化した内容が入っているので、iは１から
            //Log.d("初期化", it[0].toString())
            var i = 1
            var amount_sum = 0
            //データが0件の場合の処理
            if(it.size==1){
                income_month_text.text=item_y_string+"年"+item_m_string+"月"+"の合計収入金額は"
                income_sum_month.text = "0円"
            }
            while (i < it.size) {
                //it[i]は一件分のデータ
                //Log.d("TAG", it[i].toString())
                var j: Int = 0
                while (j < items.size) {
                    if (items[j].equals("amount")) {

                        amount_sum = amount_sum + Integer.parseInt(it[i][items[j]].toString())
                        Log.d("amount" + i + "回目", it[i][items[j]].toString())
                        Log.d("amount_sum" + i + "回目", amount_sum.toString())
                        income_month_text.text=item_y_string+"年"+item_m_string+"月"+"の合計収入金額は"
                        income_sum_month.text = amount_sum.toString() + "円"
                    } else {
                        //なにもしない
                    }
                    j++
                }
                i++
            }
        }
        ExpenseGetAmount(gmail, date1, date2) {
            //表示する項目数をここで宣言
            val items = arrayOf("date", "genre", "category", "amount", "content")
            //[件数][一件分の内容数]という2次元配列を用意する
            val Incomedata = Array(it.size, { arrayOfNulls<TextView?>(items.size) })
            //i[0]には初期化した内容が入っているので、iは１から
            //Log.d("初期化", it[0].toString())
            var i = 1
            var amount_sum = 0
            //データが0件の場合の処理
            if(it.size==1){
                expense_month_text.text=item_y_string+"年"+item_m_string+"月"+"の合計支出金額は"
                expense_sum_month.text = "0円"
            }
            while (i < it.size) {
                //it[i]は一件分のデータ
                Log.d("TAG", it[i].toString())
                var j: Int = 0
                while (j < items.size) {
                    if (items[j].equals("amount")) {

                        amount_sum = amount_sum + Integer.parseInt(it[i][items[j]].toString())
                        Log.d("amount" + i + "回目", it[i][items[j]].toString())
                        Log.d("amount_sum" + i + "回目", amount_sum.toString())
                        expense_month_text.text=item_y_string+"年"+item_m_string+"月"+"の合計支出金額は"
                        expense_sum_month.text = amount_sum.toString() + "円"
                    } else {
                        //なにもしない
                    }
                    j++
                }
                i++
            }
        }
    }

    fun SumYear(gmail: String,root:View): Unit {
        // 年を取得
        val date_y = root.findViewById(R.id.spinner_year2) as Spinner
        // 選択されているアイテムを取得
        val item_y_string = date_y.selectedItem.toString()
        val item_y = Integer.parseInt(item_y_string)
        val calendar = Calendar.getInstance()
        //              year      month     date 0にしないと検索するときの条件おかしくなる       time       minute     second
        calendar.set(item_y, 0, 0, 0, 0, 0);
        //calendar型からdate型に変換する
        val date1: Date = calendar.getTime()
        //一年間指定するために+１
        calendar.add(Calendar.YEAR, 1);
        val date2: Date = calendar.getTime()
        IncomeGetAmount(gmail, date1, date2) {
            //表示する項目数をここで宣言
            val items = arrayOf("date", "genre", "category", "amount", "content")
            //[件数][一件分の内容数]という2次元配列を用意する
            val Incomedata = Array(it.size, { arrayOfNulls<TextView?>(items.size) })
            //i[0]には初期化した内容が入っているので、iは１から
            //Log.d("初期化", it[0].toString())
            var i = 1
            var amount_sum = 0
            //データが0件の場合の処理
            if(it.size==1){
                income_year_text.text=item_y_string+"年の合計収入金額は"
                income_sum_year.text = "0円"
            }
            while (i < it.size) {
                //it[i]は一件分のデータ
                //Log.d("TAG", it[i].toString())
                var j: Int = 0
                while (j < items.size) {
                    if (items[j].equals("amount")) {

                        amount_sum = amount_sum + Integer.parseInt(it[i][items[j]].toString())
                        Log.d("amount" + i + "回目", it[i][items[j]].toString())
                        Log.d("amount_sum" + i + "回目", amount_sum.toString())
                        income_year_text.text=item_y_string+"年の合計収入金額は"
                        income_sum_year.text = amount_sum.toString() + "円"
                    } else {
                        //なにもしない
                    }
                    j++
                }
                i++
            }
        }
        ExpenseGetAmount(gmail, date1, date2) {
            //表示する項目数をここで宣言
            val items = arrayOf("date", "genre", "category", "amount", "content")
            //[件数][一件分の内容数]という2次元配列を用意する
            val Incomedata = Array(it.size, { arrayOfNulls<TextView?>(items.size) })
            //i[0]には初期化した内容が入っているので、iは１から
            //Log.d("初期化", it[0].toString())
            var i = 1
            var amount_sum = 0
            //データが0件の場合の処理
            if(it.size==1){
                expense_year_text.text=item_y_string+"年の合計支出金額は"
                expense_sum_year.text = "0円"
            }
            while (i < it.size) {
                //it[i]は一件分のデータ
                //Log.d("TAG", it[i].toString())
                var j: Int = 0
                while (j < items.size) {
                    if (items[j].equals("amount")) {

                        amount_sum = amount_sum + Integer.parseInt(it[i][items[j]].toString())
                        Log.d("amount" + i + "回目", it[i][items[j]].toString())
                        Log.d("amount_sum" + i + "回目", amount_sum.toString())
                        expense_year_text.text=item_y_string+"年の合計支出金額は"
                        expense_sum_year.text = amount_sum.toString() + "円"
                    } else {
                        //なにもしない
                    }
                    j++
                }
                i++
            }
        }
    }
    fun IncomeGetAmount(gmail:String, date1:Date,date2:Date,myCallback: (MutableList<MutableMap<String,Any>>) -> Unit) {
        //協定世界時のUTC 1970年1月1日深夜零時との差をミリ秒で取得
        val millis = System.currentTimeMillis()
        //初期化
        var maptest :MutableMap<String,Any> = mutableMapOf<String, Any>("amount" to 1000,"category" to "カテゴリ", "content" to "内容", "date" to millis, "genre" to "ジャンル", "id" to "メールアドレス")
        //mutablemapを入れる配列　複数のmapを配列で管理
        var arraytest = mutableListOf(maptest)/////ここで配列を宣言している

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("income")
            .whereEqualTo("id", gmail)
            .whereGreaterThanOrEqualTo("date", date1)
            .whereLessThan("date",  date2)
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
    fun ExpenseGetAmount(gmail:String, date1:Date,date2:Date,myCallback: (MutableList<MutableMap<String,Any>>) -> Unit) {
        //協定世界時のUTC 1970年1月1日深夜零時との差をミリ秒で取得
        val millis = System.currentTimeMillis()
        //初期化
        var maptest :MutableMap<String,Any> = mutableMapOf<String, Any>("amount" to 1000,"category" to "カテゴリ", "content" to "内容", "date" to millis, "genre" to "ジャンル", "id" to "メールアドレス")
        //mutablemapを入れる配列　複数のmapを配列で管理
        var arraytest = mutableListOf(maptest)/////ここで配列を宣言している

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("expense")
            .whereEqualTo("id", gmail)
            .whereGreaterThanOrEqualTo("date", date1)
            .whereLessThan("date",  date2)
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


}