package com.example.lesson19

import android.annotation.SuppressLint
import android.app.ActivityManager.TaskDescription
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lesson19.databinding.FragmentFirstBinding
import com.google.gson.Gson


class FirstFragment : Fragment(),TasksAdapter.OnTaskClicked {

    private var binding: FragmentFirstBinding? = null
    var adapter: TasksAdapter? = null
    private var sharePreferences: SharedPreferences? = null
    private var tasksListJson = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        sharePreferences =
            requireActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        initTasksAdapter()


        if (sharePreferences!!.getString("TASKS_JSON", "") != "") {
            val tasksListJson = Gson().fromJson(
                sharePreferences!!.getString("TASKS_JSON", ""),
                Array<Task>::class.java
            )

            adapter?.tasksList = tasksListJson.toMutableList()
        }





        if (arguments != null) {

            if (arguments?.getString("TASK_TEXT", "") != "") {

                val taskListFromJson = Gson().fromJson(
                    requireArguments().getString("TASK_LIST"),
                    Array<Task>::class.java
                )



                adapter?.tasksList = taskListFromJson.toMutableList()

                adapter?.tasksList?.add(
                    Task(
                        false,
                        requireArguments().getString("TASK_TEXT", ""),
                        requireArguments().getString("TASK_DESCRIPTION","")
                    )
                )

                tasksListJson = Gson().toJson(adapter?.tasksList)
            }
        }
    }

    private fun initTasksAdapter() {
        adapter = TasksAdapter()
        binding?.rvToDoList?.adapter = adapter
        binding?.rvToDoList?.layoutManager = LinearLayoutManager(requireContext())
        adapter?.onTaskClickedImpl=this
    }

    private fun setOnClickListener() {
        binding?.btnToDoList?.setOnClickListener() {

            tasksListJson = Gson().toJson(adapter?.tasksList)

            val bundle = Bundle()
            bundle.putString("TASK_LIST", tasksListJson)
            val fragment = AddTaskFragment()
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment).commit()
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStop() {
        tasksListJson = Gson().toJson(adapter?.tasksList)
        sharePreferences?.edit()?.putString("TASKS_JSON", tasksListJson)?.apply()

        super.onStop()
    }

    override fun onTaskClicked(taskName: String, taskDescription:String) {
        val bundle = Bundle()
        bundle.putString("TASK_NAME", taskName)
        bundle.putString("TASK_DESCRIPTION",taskDescription)
        bundle.putBoolean("AFTER_TASK_IS_CLICKED",true )



        val fragment = AddTaskFragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).addToBackStack("").commit()
    }

}