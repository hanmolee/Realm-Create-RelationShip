package com.hanmo.testforlinkingobject

import android.support.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by hanmo on 2018. 3. 26..
 */
class HanmoApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        initRealm()
    }

    private fun initRealm() {

        Realm.init(this)

        val config = RealmConfiguration.Builder()
                .name("hanmo.realm")
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)

    }

    override fun onTerminate() {
        super.onTerminate()
        if (!Realm.getDefaultInstance().isClosed) {
            Realm.getDefaultInstance().close()
        }
    }
}