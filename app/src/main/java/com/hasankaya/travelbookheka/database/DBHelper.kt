package com.hasankaya.travelbookheka.database
//https://medium.com/@ekrem.hatipoglu/kotlin-android-sqlite-veri-taban%C4%B1-kullan%C4%B1m%C4%B1-c5c427d29b71
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract
import com.hekasoftdesign.containerappdemo.model.MyContainer


class DBHelper(val context: Context) : SQLiteOpenHelper(context,DBHelper.DATABASE_NAME,null,DBHelper.DATABASE_VERSION) {
    private val TABLE_NAME="Containers"
    private val COL_ContainerID = "ContainerID"
    private val COL_CDate = "CDate"
    private val COL_SensorID = "SensorID"
    private val COL_OccupancyRate = "OccupancyRate"
    private val COL_temperature = "temperature"
    private val COL_Clatitute = "Clatitute"
    private val COL_Clongtitude = "Clongtitude "
    private val COL_CName = "CName"
    companion object {
        private val DATABASE_NAME = "SQLITE_DATABASE"//database adı
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COL_ContainerID INTEGER PRIMARY KEY , " +
                "$COL_CDate  VARCHAR(256),$COL_SensorID  VARCHAR(256),$COL_OccupancyRate  INTEGER," +
                "$COL_temperature  INTEGER,$COL_Clatitute REAL," +
                "$COL_Clongtitude REAL,$COL_CName  VARCHAR(256))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
//depend on internet status
    fun InsertContainer(contanier: MyContainer):Long{
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ContainerID , contanier.ContainerID)
        contentValues.put(COL_CDate,contanier.CDate)
        contentValues.put(COL_SensorID,contanier.SensorID)
        contentValues.put(COL_OccupancyRate,contanier.OccupancyRate)
        contentValues.put(COL_temperature,contanier.temperature)
        contentValues.put(COL_Clatitute,contanier.Clatitute)
        contentValues.put(COL_Clongtitude,contanier.Clongtitude)
        contentValues.put(COL_CName,contanier.CName)

        var result = sqliteDB.insert(TABLE_NAME,null, contentValues)
        return result
    }

}