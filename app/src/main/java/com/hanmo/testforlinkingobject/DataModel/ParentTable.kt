package com.hanmo.testforlinkingobject.DataModel

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by hanmo on 2018. 3. 26..
 */
open class ParentTable : RealmObject() {

    @PrimaryKey
    open var id: Long = 0

    open var parentName : String? = null

    var child: RealmList<ChildTable>? = RealmList()
}