package com.hanmo.testforlinkingobject

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.hanmo.testforlinkingobject.DataModel.ChildTable
import com.hanmo.testforlinkingobject.DataModel.ParentTable
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Created by hanmo on 2018. 4. 6..
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RealmTransactionTest {

    lateinit var realm : Realm
    private val parentId : Long = 1
    private val childId : Long = 1

    @Before
    fun initDB() {
        Realm.init(InstrumentationRegistry.getTargetContext())

        val config = RealmConfiguration.Builder()
                .name("test.realm")
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)

        realm = Realm.getDefaultInstance()
    }

    @Test
    fun AinsertParent() {

        val parent = ParentTable() // realm.createObject(Parent::class.java)
        parent.id = parentId
        parent.parentName = "test_ParentName"

        realm.executeTransaction {
            it.copyToRealmOrUpdate(parent)
        }

        val testParentQuery = realm.where(ParentTable::class.java).equalTo("id", parentId).findFirst()
        assertNotNull(testParentQuery)
        testParentQuery?.let {
            assertEquals(parentId, it.id)
            assertEquals("test_ParentName", it.parentName)
        }
    }

    @Test
    fun BinsertChild() {

        val child = ChildTable() //realm.createObject(ChildTable::class.java)
        child.id = childId
        child.childName = "test_ChildName"
        child.parentName = "test_ParentName"

        var parent = realm.where(ParentTable::class.java).equalTo("parentName", "test_ParentName").findFirst()

        realm.executeTransaction {
            parent?.child?.add(child)
            realm.copyToRealmOrUpdate(child)
        }

        //Child Data가 제대로 들어갔는지
        //들어갔다면 ParentTable과의 연동이 제대로 되고있는지
        val testChildQuery = realm.where(ChildTable::class.java).equalTo("id", childId).findFirst()
        assertNotNull(testChildQuery)
        testChildQuery?.let {
            assertEquals(childId, it.id)
            assertEquals("test_ChildName", it.childName)
            assertEquals("test_ParentName", it.parentName)

            assertNotNull(it.parent)
            it.parent?.forEach {
                assertEquals(parentId, it.id)
                assertEquals("test_ParentName", it.parentName)
            }
        }

        //ParentTable에서 Child Data가 제대로 들어갔는지 확인
        val testParentQuery = realm.where(ParentTable::class.java).equalTo("parentName", "test_ParentName").findAll()
        assertNotNull(testParentQuery)
        assertNotEquals(true, testParentQuery.isEmpty())

        testParentQuery.forEach {
            assertNotNull(it.child)
            assertNotEquals(true, it.child?.isEmpty())
            it.child?.forEach {
                assertEquals(childId, it.id)
                assertEquals("test_ChildName", it.childName)
                assertEquals("test_ParentName", it.parentName)
            }
        }

    }

    @Test
    fun CupdateChild() {
        val child = realm.where(ChildTable::class.java).equalTo("childName", "test_ChildName").findFirst()

        assertNotNull(child)

        realm.executeTransaction {
            child?.childName = "test_UpdateChildName"
        }

        val testChildQuery = realm.where(ChildTable::class.java).equalTo("id", childId).findFirst()
        assertNotNull(testChildQuery)
        testChildQuery?.let {
            assertEquals(childId, it.id)
            assertEquals("test_UpdateChildName", it.childName)
            assertEquals("test_ParentName", it.parentName)

            assertNotNull(it.parent)
            it.parent?.forEach {
                assertEquals(parentId, it.id)
                assertEquals("test_ParentName", it.parentName)
            }
        }

        val testParentQuery = realm.where(ParentTable::class.java).equalTo("parentName", "test_ParentName").findAll()
        assertNotNull(testParentQuery)
        assertNotEquals(true, testParentQuery.isEmpty())
        testParentQuery.forEach {
            assertNotNull(it.child)
            assertNotEquals(true, it.child?.isEmpty())
            it.child?.forEach {
                assertEquals(childId, it.id)
                assertEquals("test_UpdateChildName", it.childName)
                assertEquals("test_ParentName", it.parentName)
            }
        }
    }

    @Test
    fun DdeleteChild() {

        val testQuery = realm.where(ParentTable::class.java).equalTo("id", parentId).findFirst()
        assertNotNull(testQuery)
        testQuery?.let {
            it.child?.let {
                assertNotNull(it.where().equalTo("id", childId).findFirst())
            }
        }

        val childTable = realm.where(ChildTable::class.java).equalTo("id", childId).findFirst()
        realm.executeTransaction {
            childTable?.deleteFromRealm()
        }

        val childTable2 = realm.where(ChildTable::class.java).equalTo("id", childId).findFirst()
        assertNull(childTable2)

        val testQuery2 = realm.where(ParentTable::class.java).equalTo("id", parentId).findFirst()
        assertNotNull(testQuery)
        testQuery2?.let {
            it.child?.let {
                assertNull(it.where().equalTo("id", childId).findFirst())
            }
        }
    }

    @Test
    fun Zdelete() {
        Log.e("delete", "delete")
        val parentTable = realm.where(ParentTable::class.java).findAll()
        val childTable = realm.where(ChildTable::class.java).findAll()

        realm.executeTransaction {
            parentTable.deleteAllFromRealm()
            childTable.deleteAllFromRealm()
        }
    }

    @After
    fun closeDB() {
        assertNotEquals(true, realm.isClosed)
        if (!realm.isClosed){
            realm.close()
            assertEquals(true, realm.isClosed)
        }
    }

}