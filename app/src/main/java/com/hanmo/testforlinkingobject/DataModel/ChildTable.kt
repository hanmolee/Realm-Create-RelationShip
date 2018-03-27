package com.hanmo.testforlinkingobject.DataModel

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

/**
 * Created by hanmo on 2018. 3. 26..
 */
open class ChildTable : RealmObject() {

    @PrimaryKey
    open var id : Long = 0
    
    open var childName : String? = null

    open var parentName : String? = null
    
    @LinkingObjects("child")
    val parent: RealmResults<ParentTable>? = null
}