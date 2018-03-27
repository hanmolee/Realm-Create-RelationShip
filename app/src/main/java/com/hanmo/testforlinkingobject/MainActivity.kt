package com.hanmo.testforlinkingobject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hanmo.testforlinkingobject.DataModel.ChildTable
import com.hanmo.testforlinkingobject.DataModel.ParentTable
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by hanmo on 2018. 3. 26..
 */
class MainActivity : AppCompatActivity() {

    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compositeDisposable = CompositeDisposable()

        setButtonClick()
    }

    private fun setButtonClick() {

        btn_create_parent.clicks()
                .subscribe {
                    RealmHelper.instance.insertParent(et_parent_name.text.toString())
                }.apply { compositeDisposable.add(this) }

        btn_create_child.clicks()
                .subscribe {
                    RealmHelper.instance.insertChild(et_child_name.text.toString(), et_parent_name_fk.text.toString())
                }.apply { compositeDisposable.add(this) }

        btn_update_parent.clicks()
                .subscribe {
                    RealmHelper.instance.updateParent(et_parent_name.text.toString())
                }.apply { compositeDisposable.add(this) }

        btn_update_child.clicks()
                .subscribe {
                    RealmHelper.instance.updateChild(et_child_name.text.toString())
                }.apply { compositeDisposable.add(this) }

        btn_delete_parent.clicks()
                .subscribe {
                    RealmHelper.instance.deleteParent(et_parent_name.text.toString())
                }.apply { compositeDisposable.add(this) }

        btn_delete_child.clicks()
                .subscribe {
                    RealmHelper.instance.deleteChild(et_child_name.text.toString())
                }.apply { compositeDisposable.add(this) }

        btn_query1.clicks()
                .subscribe {
                    val parent = RealmHelper.instance.queryAll(ParentTable::class.java)
                    val child = RealmHelper.instance.queryAll(ChildTable::class.java)


                    val parentChild = StringBuffer()
                    val childParent = StringBuffer()

                    parent?.forEach {
                        it.child?.forEach {
                            parentChild.append(it.toString())
                        }
                    }
                    child?.forEach {
                        childParent.append(it.parent.toString())
                    }

                    txt_result.text =
                            "parent : $parent \n" +
                            "parent.child : $parentChild \n" +
                            "child : $child \n" +
                            "child.parent : $childParent"

                }.apply { compositeDisposable.add(this) }

        btn_query2.clicks()
                .subscribe{

                    val child = RealmHelper.instance.queryChildRelationship(et_parent_name.text.toString())
                    val parent = RealmHelper.instance.queryParentRelationship(et_parent_name.text.toString())

                    txt_result.text =
                            "parentName : ${et_parent_name.text} \n" +
                            "parent : $parent \n" +
                            "child : $child \n"

                }.apply { compositeDisposable }

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}