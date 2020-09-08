package com.example.you_tulle

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_set_customization.*
import java.util.*
import kotlin.collections.ArrayList

class SetCustomization : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_customization)

        val GoogleId = "123@gmail.com"
        //カテゴリのspinner
        readSetCategory(GoogleId) {

            val documentid:ArrayList<String> = it[0] as ArrayList<String> //ドキュメントのＩＤの取得

            //今回は検索結果が一件しか取得できない一つのメールアドレスに一つだけ
            val fielddata: MutableList<MutableMap<String, Any>> = it[1] as MutableList<MutableMap<String, Any>>
            //Log.d("カスタマイズカテゴリ全体", fielddata.toString())

            //初めてカスタマイズを使うとき、firebaseにデータがないので取り出せない。
            //データを取り出して配列にいれることができないので、
            //参照できないことを条件に初期化の処理をする。
            try {
                val fielddata1 = fielddata[1]

                //firestoreに入っている配列はlistで返ってくるのでList<String>にキャストする。
                val setcategorybox = fielddata1["category"] as ArrayList<String>
                //Log.d("カスタマイズカテゴリ一覧", setcategorybox.toString())

                // FrameLayout のインスタンスを取得しています
                // textsWindow は予めレイアウトファイル(.xml)に定義したものです
                val layout: FrameLayout = findViewById(R.id.categoryWindow)

                //表示するFrameLayoutの高さを要素この数で動的に変更する
                val param = layout.getLayoutParams()
                param.height = 150+90*(setcategorybox.size)

                // それぞれのTextView に設定を行い、FrameLayout のインスタンスに追加しています
                var i:Int = 0
                val categorydata:Array<TextView?> =arrayOfNulls<TextView?>(setcategorybox.size)
                val deletebuttondata:Array<Button?> =arrayOfNulls<Button?>(setcategorybox.size)
                //カスタマイズカテゴリにデータが一件もなかった場合の処理
                if(setcategorybox.size==0){
                    val nodata = TextView(this)
                    nodata?.setTextColor(Color.BLACK)
                    nodata?.x = 80.0f
                    nodata?.y = 80.0f
                    nodata?.text = "カスタマイズカテゴリを追加してください。"
                    layout.addView(
                        nodata,
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50)
                    )
                }
                while(i<setcategorybox.size) {
                    categorydata[i] = TextView(this)
                    categorydata[i]?.text = setcategorybox[i]
                    categorydata[i]?.setTextColor(Color.BLACK)
                    categorydata[i]?.setGravity(Gravity.CENTER);
                    categorydata[i]?.x = 50.0f
                    categorydata[i]?.y = 85.0f * (i + 1)
                    layout.addView(
                        categorydata[i],
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50)
                    )
                    val button = Button(this)
                    button.text = getString(R.string.button)

                    deletebuttondata[i] = Button(this)
                    deletebuttondata[i]?.text = "削除"
                    deletebuttondata[i]?.x = 550.0f
                    deletebuttondata[i]?.y = 85.0f * (i + 1)
                    layout.addView(
                        deletebuttondata[i],
                        LinearLayout.LayoutParams(150, 80)
                    )
                    //Log.d("削除するもの", categorydata[i]?.text.toString())
                    //iをそのまま使って指定したらエラーでたから何個目かを表す数値を入れる変数
                    var countitem=i
                    deletebuttondata[i]?.setOnClickListener {
                        setcategorybox.remove( categorydata[countitem]?.text.toString() )
                        InsertCategory(GoogleId,setcategorybox,documentid[0])
                        // intent 設定で自分自身のクラスを設定 画面の更新をする
                        val intent = Intent(this, SetCustomization::class.java)
                        startActivity(intent)
                    }
                    i++
                }
                //自作カテゴリの追加
                insertcategory.setOnClickListener {
                    val addcategoryitem = findViewById(R.id.contenttext)as EditText
                    //追加する項目がなにもないときは処理をしない。
                    if(!Objects.equals(addcategoryitem.text.toString(), "")) {
                        setcategorybox.add(addcategoryitem.text.toString())
                        InsertCategory(GoogleId, setcategorybox, documentid[0])
                        // intent 設定で自分自身のクラスを設定　画面の更新をする
                        val intent = Intent(this, SetCustomization::class.java)
                        startActivity(intent)
                    }
                }
            }catch(e: IndexOutOfBoundsException){
                //初めて自作カテゴリを利用するとき、categoryに初期化したデータを入れる。
                InsertCustomization(GoogleId )
            }
        }

        //Spinnerへ画面遷移をする関数
        fun IntentToTop(view: View?){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        ToTop.setOnClickListener{IntentToTop(it)}
    }

    fun readSetCategory(gmail:String,myCallback: (ArrayList<Any?>) -> Unit) {

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
                    //[0]にドキュメントＩＤ　[1]にフィールドデータ
                    databox.add(document_id_list )
                    databox.add(arraytest)
                    myCallback(databox)
                }
            }
    }
    fun InsertCategory(GoogleId:String,setcategorybox:List<String>,documentid:String):Unit{
        val TAG="カスタマイズカテゴリ登録"

        //フィールド名
        val Item = hashMapOf(
            "id" to GoogleId,
            "category" to setcategorybox
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("expense_customized")   //"コレクション名になる

            .document(documentid)
            .set(
                Item
            )
            .addOnSuccessListener { documentReference -> //タスクが正常に完了したときに呼び出されるリスナー。
                Log.d(TAG, "カスタマイズカテゴリの登録に成功しました。")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "カスタマイズカテゴリの登録に失敗しました。", e)
            }
    }
    //カスタマイズを初めて使うときfirebaseにテンプレートを生成
    fun InsertCustomization(GoogleId:String ):Unit{
        val TAG="新規カスタマイズカテゴリ生成"
        //カスタマイズの初期化
        var IniCustomization  =  arrayListOf("自由にカテゴリを変更してください。")
        //フィールド名
        val Item = hashMapOf(
            "id" to GoogleId,
            "category" to IniCustomization
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("expense_customized")   //"コレクション名になる
            // .document("ドキュメント名")でドキュメントを自分で設定することができる        ドキュメントを自分で設定した場合、.appではなく.setに変更する
            .add(Item)//フィールド名
            .addOnSuccessListener { documentReference -> //タスクが正常に完了したときに呼び出されるリスナー。
                Log.d(TAG, "新規カスタマイズカテゴリの登録に成功しました。")
                val intent = Intent(this, SetCustomization::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "新規カスタマイズカテゴリの登録に失敗しました。", e)
            }
    }
}