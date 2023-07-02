package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.Result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)

@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var Repo: RemindersLocalRepository
    private lateinit var database: RemindersDatabase
    private lateinit var  rem: ReminderDTO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        Repo = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
        rem = ReminderDTO("Home", "MyLocation", "el nozha", 55.99933, 66.33344)
    }

    @After
    fun closedao() {
        database.close()
    }

    @Test
    fun putRemInDao_GetById() = runBlocking {
        Repo.saveReminder(rem)

        val dataList = Repo.getReminder(rem.id) as? Result.Success

        assertThat(dataList is Result.Success, CoreMatchers.`is`(true))
        dataList as Result.Success
        assertThat(dataList?.data?.title, CoreMatchers.`is`(rem.title))
        assertThat(dataList?.data?.description, CoreMatchers.`is`(rem.description))
        assertThat(dataList?.data?.latitude, CoreMatchers.`is`(rem.latitude))
        assertThat(dataList?.data?.longitude, CoreMatchers.`is`(rem.longitude))
        assertThat(dataList?.data?.location, CoreMatchers.`is`(rem.location))
    }


    @Test
    fun getReminder() = runBlocking {
        Repo.saveReminder(rem)
        Repo.deleteAllReminders()
        val getReminder = Repo.getReminder(rem.id)
        assertThat(getReminder is Result.Error, `is`(true))
        getReminder as Result.Error
        assertThat(getReminder.message, `is`("Reminder not found!"))
    }


    @Test
    fun delete_All_Reminder()= runBlocking {
        Repo.saveReminder(rem)
        Repo.deleteAllReminders()
        val dataList = Repo.getReminders()
        assertThat(dataList is Result.Success, `is`(true))
        dataList as Result.Success
        assertThat(dataList.data, `is`(emptyList()))
    }

}