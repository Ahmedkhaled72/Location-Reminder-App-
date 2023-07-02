package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (var reminderList: MutableList<ReminderDTO>? = mutableListOf()): ReminderDataSource {

    private var shouldReturnError = false

      fun setReturnError(result: Boolean)
      {
          shouldReturnError = result
      }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {

       if(shouldReturnError) {
            return Result.Error("Remeinder not found")
       }
          reminderList?.let { return Result.Success(it) }
        return Result.Error("Is Not Found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {

        reminderList?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        val rem =  reminderList?.find { remDto -> remDto.id == id  }

        return when{
             shouldReturnError -> {Result.Error("Reminder return Error")}
             rem !=null -> {Result.Success(rem)}
             else -> {Result.Error("Reminder not found")}
        }
    }

    override suspend fun deleteAllReminders() {

        reminderList?.clear()
    }


}