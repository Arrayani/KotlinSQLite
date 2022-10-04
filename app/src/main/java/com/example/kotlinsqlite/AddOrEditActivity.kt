package com.example.kotlinsqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.kotlinsqlite.databinding.ActivityAddOrEditBinding
import com.example.kotlinsqlite.db.DatabaseHandler
import com.example.kotlinsqlite.models.Tasks

class AddOrEditActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddOrEditBinding
    var dbHandler: DatabaseHandler? = null
    var isEditMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initDB()
        initOperations()
    }

    private fun initOperations() {
        val btn_save = binding.btnSave
        btn_save.setOnClickListener {
            var success: Boolean = false
            if (!isEditMode) {
                val tasks: Tasks = Tasks()
                val input_name = binding.inputName
                val input_desc = binding.inputDesc
                val swt_completed = binding.swtCompleted
                tasks.name = input_name.text.toString()
                tasks.desc = input_desc.text.toString()
                if (swt_completed.isChecked)
                    tasks.completed = "Y"
                else
                    tasks.completed = "N"
                success = dbHandler?.addTask(tasks) as Boolean
            } else {
                val tasks: Tasks = Tasks()
                tasks.id = intent.getIntExtra("Id", 0)
                val input_name = binding.inputName
                val input_desc = binding.inputDesc
                val swt_completed = binding.swtCompleted
                tasks.name = input_name.text.toString()
                tasks.desc = input_desc.text.toString()
                if (swt_completed.isChecked)
                    tasks.completed = "Y"
                else
                    tasks.completed = "N"
                success = dbHandler?.updateTask(tasks) as Boolean
            }

            if (success)
                finish()
        }

        val btn_delete = binding.btnDelete
        btn_delete.setOnClickListener {
            val dialog = AlertDialog.Builder(this).setTitle("Info")
                .setMessage("Click 'YES' Delete the Task.")
                .setPositiveButton("YES") { dialog, i ->
                    val success = dbHandler?.deleteTask(intent.getIntExtra("Id", 0)) as Boolean
                    if (success)
                        finish()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, i ->
                    dialog.dismiss()
                }
            dialog.show()
        }
    }

    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        val btn_delete = binding.btnDelete
        btn_delete.visibility = View.INVISIBLE
        if (intent != null && intent.getStringExtra("Mode") == "E") {
            isEditMode = true
            val tasks: Tasks = dbHandler!!.getTask(intent.getIntExtra("Id",0))
            val input_name = binding.inputName
            val input_desc = binding.inputDesc
            val swt_completed = binding.swtCompleted
            input_name.setText(tasks.name)
            input_desc.setText(tasks.desc)
            swt_completed.isChecked = tasks.completed == "Y"
            btn_delete.visibility = View.VISIBLE
        }
    }
}