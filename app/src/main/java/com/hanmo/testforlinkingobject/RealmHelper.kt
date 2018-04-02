package com.hanmo.testforlinkingobject

import android.util.Log
import com.hanmo.testforlinkingobject.DataModel.ChildTable
import com.hanmo.testforlinkingobject.DataModel.ParentTable
import io.realm.*

/**
 * Created by hanmo on 2018. 3. 26..
 */
class RealmHelper {

    var realm: Realm
        private set

    init {
        realm = try {

            Realm.getDefaultInstance()

        } catch (e: Exception) {

            Log.d("Realm Exception", e.toString())

            val config = RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build()
            Realm.getInstance(config)
        }
    }

    fun selectTables() {
        val parent = queryAll(ParentTable::class.java)
        Log.e("parent", parent.toString())

        parent?.forEach {
            it.child?.forEach {
                Log.e("parent.child", "id : $it.id, childName : ${it.childName}")
            }

        }

        val child = queryAll(ChildTable::class.java)
        Log.e("child", child.toString())

        child?.forEach {
            it.parent?.forEach {
                Log.e("child.parent", "id : $it.id, parentName : ${it.parentName}")
            }
        }
    }

    fun insertParent(parentName: String) {

        val maxId = realm.where(ParentTable::class.java).max("id")
        val nextId : Long =
                when(maxId) {
                    null -> { 1 }
                    else -> { maxId.toLong() + 1 }
                }

        val parent = ParentTable() // realm.createObject(Parent::class.java)
        parent.id = nextId
        parent.parentName = parentName

        addRealmListData(parent)

    }


    fun insertChild(childName: String, parentName: String) {
        val maxId = realm.where(ChildTable::class.java).max("id")
        val nextId : Long =
                when(maxId) {
                    null -> { 1 }
                    else -> { maxId.toLong() + 1 }
                }

        val child = ChildTable() //realm.createObject(ChildTable::class.java)
        child.id = nextId
        child.childName = childName
        child.parentName = parentName

        var parent = realm.where(ParentTable::class.java).equalTo("parentName", parentName).findFirst()

        realm.executeTransaction {
            parent?.child?.add(child)
            realm.copyToRealmOrUpdate(child)
        }
    }

    fun updateChild(childName: String) {

        val child = realm.where(ChildTable::class.java).findFirst()

        realm.executeTransaction {
            child?.childName = childName
        }
    }

    fun updateParent(parentName: String) {

        val parent = realm.where(ParentTable::class.java).findFirst()

        realm.executeTransaction {
            parent?.parentName = parentName
        }
    }

    fun deleteParent(parentName: String) {

        val parent = realm.where(ParentTable::class.java).equalTo("parentName", parentName).findFirst()

        realm.executeTransaction {
            parent?.deleteFromRealm()
        }
    }

    fun deleteChild(childName: String) {

        val child = realm.where(ChildTable::class.java).equalTo("childName", childName).findFirst()
        val parent = realm.where(ParentTable::class.java).equalTo("child.childName", childName).findAll()

        realm.executeTransaction {
            child?.deleteFromRealm()
            parent?.deleteAllFromRealm()
        }



    }

    fun queryChildRelationship(parentName: String): RealmResults<ChildTable> {
        return realm.where(ChildTable::class.java).equalTo("parent.parentName", parentName).findAll()
    }

    fun queryParentRelationship(parentName: String): RealmResults<ParentTable> {
        return realm.where(ParentTable::class.java).equalTo("child.parentName", parentName).findAll()
    }

    //Insert To Realm
    fun <T : RealmObject> addData(data: T) {
        realm.executeTransaction {
            realm.copyToRealm(data)
        }
    }

    //Insert To Realm with RealmList
    fun <T : RealmObject> addRealmListData(data: T) {
        realm.executeTransaction {
            realm.copyToRealmOrUpdate(data)
        }
    }

    fun <T : RealmObject> queryAll(clazz: Class<T>): RealmResults<T>? {
        return realm.where(clazz).findAll()
    }

    companion object {

        private var INSTANCE: RealmHelper? = RealmHelper()

        val instance: RealmHelper
            get() {
                if (INSTANCE == null) {
                    INSTANCE = RealmHelper()
                }
                return INSTANCE as RealmHelper
            }
    }

}


