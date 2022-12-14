package com.example.kotlinsqlite.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import com.example.kotlinsqlite.models.Tasks
import java.util.*

/**

 * Author: Roni Reynal Fitri  on $ {DATE} $ {HOUR}: $ {MINUTE}

 * Email: biruprinting@gmail.com

 */
class DatabaseHandler(context: Context)
    : SQLiteOpenHelper(context, DatabaseHandler.DB_NAME, null, DatabaseHandler.DB_VERSION) {
    companion object {
        private val DB_VERSION = 1
        private val DB_NAME = "MyTasks"
        private val TABLE_NAME = "Tasks"
        private val ID = "Id"
        private val NAME = "Name"
        private val DESC = "Desc"
        private val COMPLETED = "Completed"
    }
    //dibagian $DESC TEXt ada peringatan, tapi kok tetap bisa berjalan ini aplikasi
    // ini sepertinya sebuag bug
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT, $DESC TEXT, $COMPLETED TEXT);"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addTask(tasks: Tasks): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()  // ini mirip hash map di inserting data ke firebase
        values.put(NAME, tasks.name)
        values.put(DESC, tasks.desc)
        values.put(COMPLETED, tasks.completed)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()  // ada rules harus close koneksi, setelah melakukan transaksi di database
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1) //jika sukses atau true maka nilai value bukan -1
    }

    fun getTask(_id: Int): Tasks {
        val tasks = Tasks()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $ID = $_id"
        val cursor = db.rawQuery(selectQuery, null)

        cursor?.moveToFirst()
        tasks.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
        tasks.name = cursor.getString(cursor.getColumnIndex(NAME))
        tasks.desc = cursor.getString(cursor.getColumnIndex(DESC))
        tasks.completed = cursor.getString(cursor.getColumnIndex(COMPLETED))
//        tasks.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ID)))
//        tasks.name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
//        tasks.desc = cursor.getString(cursor.getColumnIndexOrThrow(DESC))
//        tasks.completed = cursor.getString(cursor.getColumnIndexOrThrow(COMPLETED))
        cursor.close()
        return tasks
    }

    fun task(): List<Tasks> {
        val taskList = ArrayList<Tasks>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val tasks = Tasks()
//                    tasks.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
//                    tasks.name = cursor.getString(cursor.getColumnIndex(NAME))
//                    tasks.desc = cursor.getString(cursor.getColumnIndex(DESC))
//                    tasks.completed = cursor.getString(cursor.getColumnIndex(COMPLETED))
                    tasks.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ID)))
                    tasks.name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                    tasks.desc = cursor.getString(cursor.getColumnIndexOrThrow(DESC))
                    tasks.completed = cursor.getString(cursor.getColumnIndexOrThrow(COMPLETED))
                    taskList.add(tasks)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return taskList
    }

    fun updateTask(tasks: Tasks): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME, tasks.name)
        values.put(DESC, tasks.desc)
        values.put(COMPLETED, tasks.completed)
        val _success = db.update(TABLE_NAME, values, ID + "=?", arrayOf(tasks.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deleteTask(_id: Int): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, ID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deleteAllTasks(): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, null, null).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }


}