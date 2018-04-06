package com.hanmo.testforlinkingobject

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.hanmo.testforlinkingobject.DataModel.ChildTable
import com.hanmo.testforlinkingobject.DataModel.ParentTable
import io.realm.Realm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by hanmo on 2018. 4. 6..
 */
@RunWith(AndroidJUnit4::class)
class RealmTransactionTest {

    lateinit var realm : Realm

    @Before
    fun initDB() {
        Realm.init(InstrumentationRegistry.getTargetContext())
        realm = Realm.getDefaultInstance()
    }

    @Test
    fun insertParent() {

        val maxId = realm.where(ParentTable::class.java).max("id")
        val nextId : Long =
                when(maxId) {
                    null -> { 1 }
                    else -> { maxId.toLong() + 1 }
                }

        val parent = ParentTable() // realm.createObject(Parent::class.java)
        parent.id = nextId
        parent.parentName = "test_ParentName"

        realm.executeTransaction {
            it.copyToRealmOrUpdate(parent)
        }

        val testQuery = realm.where(ParentTable::class.java).equalTo("id", nextId).findFirst()
        assertNotNull(testQuery)
        testQuery?.let {
            assertEquals(nextId, it.id)
            assertEquals("test_ParentName", it.parentName)
        }
    }

    @Test
    fun insertChild() {
        val maxId = realm.where(ChildTable::class.java).max("id")
        val nextId : Long =
                when(maxId) {
                    null -> { 1 }
                    else -> { maxId.toLong() + 1 }
                }

        val child = ChildTable() //realm.createObject(ChildTable::class.java)
        child.id = nextId
        child.childName = "test_ChildName"
        child.parentName = "test_ParentName"

        var parent = realm.where(ParentTable::class.java).equalTo("parentName", "test_ParentName").findFirst()

        realm.executeTransaction {
            parent?.child?.add(child)
            realm.copyToRealmOrUpdate(child)
        }

        val testQuery = realm.where(ChildTable::class.java).equalTo("id", nextId).findFirst()
        testQuery?.let {
            assertEquals(nextId, it.id)
            assertEquals("test_ChildName", it.childName)
            assertEquals("test_ParentName", it.parentName)

            assertNotNull(it.parent)
            it.parent?.forEach {
                assertEquals(1, it.id)
                assertEquals("test_ParentName", it.parentName)
            }
        }

    }

}