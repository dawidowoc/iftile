package com.github.dawidowoc.iftile

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.widget.TextView
import com.github.dawidowoc.iftile.databinding.ActivityMainBinding
import com.github.dawidowoc.iftile.model.IntermittentFastingTimeConfig
import com.github.dawidowoc.iftile.persistence.FastingTimeConfigDao
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.time.LocalTime

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fastingTimeConfigDao: FastingTimeConfigDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fastingTimeConfigDao = FastingTimeConfigDao(
            PreferenceManager
                .getDefaultSharedPreferences(this)
        )

        val fastingStartTimeValueView = findViewById<TextView>(R.id.fasting_start_time_value)
        val fastingEndTimeValueView = findViewById<TextView>(R.id.fasting_end_time_value)
        val fastingStartTimeViewGroup = findViewById<ViewGroup>(R.id.fasting_start_time_viewgroup)
        val fastingEndTimeViewGroup = findViewById<ViewGroup>(R.id.fasting_end_time_viewgroup)

        val fastingTimeConfigSubject =
            BehaviorSubject.createDefault(fastingTimeConfigDao.getConfig())

        fastingTimeConfigSubject.subscribe {
            fastingStartTimeValueView.text = it.fastingStartTime.toString()
            fastingEndTimeValueView.text = it.fastingEndTime.toString()
        }

        fastingStartTimeViewGroup.setOnClickListener {
            showFastingTimePickerDialog(
                fastingTimeConfigDao.getConfig().fastingStartTime,
                fastingTimeConfigSubject
            )
            { hour, minute ->
                IntermittentFastingTimeConfig(
                    LocalTime.of(hour, minute),
                    fastingTimeConfigDao.getConfig().fastingEndTime
                )
            }
        }

        fastingEndTimeViewGroup.setOnClickListener {
            showFastingTimePickerDialog(
                fastingTimeConfigDao.getConfig().fastingEndTime,
                fastingTimeConfigSubject
            )
            { hour, minute ->
                IntermittentFastingTimeConfig(
                    fastingTimeConfigDao.getConfig().fastingStartTime,
                    LocalTime.of(hour, minute)
                )
            }
        }
    }

    private fun showFastingTimePickerDialog(
        fastingTime: LocalTime,
        fastingTimeConfigSubject: BehaviorSubject<IntermittentFastingTimeConfig>,
        fastingTimeConfigCreator: (Int, Int) -> IntermittentFastingTimeConfig
    ) {
        showTimePickerDialog(fastingTime.hour, fastingTime.minute) { hour, minute ->
            fastingTimeConfigDao.setConfig(fastingTimeConfigCreator(hour, minute))
            fastingTimeConfigSubject.onNext(fastingTimeConfigDao.getConfig())
        }
    }

    private fun showTimePickerDialog(
        initHour: Int,
        initMinute: Int,
        onTimeChange: (Int, Int) -> Unit
    ) {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour: Int, minute: Int -> onTimeChange(hour, minute) },
            initHour,
            initMinute, true
        )
        timePickerDialog.show()
    }
}
