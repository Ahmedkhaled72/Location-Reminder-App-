package com.udacity.project4.locationreminders.data.local
import com.udacity.project4.locationreminders.data.dto.Result
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var Repo:RemindersLocalRepository
    private lateinit var dataTest: ReminderDTO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        Repo= RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)
        dataTest = ReminderDTO("Location", "MyLocation","El nozha",22.32320,23.32320,"id")
    }

    @After
    fun closeDb() = database.close()

    @Test fun insertByIdRem() = runBlockingTest {
        val rem = ReminderDTO("Location", "My Location","dessk",77.32320,66.32320,"id");
        database.reminderDao().saveReminder(rem)

        //WHEN
        val datta = database.reminderDao().getReminderById(rem.id)

        //THEN
        Assert.assertThat<ReminderDTO>(datta as ReminderDTO, notNullValue())
        Assert.assertThat(datta.id, `is`(rem.id))
        Assert.assertThat(datta.title, `is`(rem.title))
        Assert.assertThat(datta.description, `is`(rem.description))
        Assert.assertThat(datta.latitude, `is`(rem.latitude))
        Assert.assertThat(datta.longitude, `is`(rem.longitude))
    }

    @Test fun checkNotFountRemById() = runBlockingTest{
        val data = Repo.getReminder("1")
        assertThat(data is Result.Error, `is`(true))
        data as Result.Error
        assertThat(data.message, `is`("Reminder not found!"))
    }

    @Test fun checkEmptyReminder() = runBlockingTest{
        val data = Repo.getReminders()
        assertThat(data is Result.Success, `is`(true))
        data as Result.Success
        assertThat(data.data, `is` (emptyList()))
    }


    @Test
    fun checkEmptyDao() = runBlockingTest{
        database.reminderDao().saveReminder(dataTest)

        //When
        val data = database.reminderDao().getReminderById(dataTest.id)

        //THEN
        Assert.assertThat<ReminderDTO>(data as ReminderDTO, notNullValue())
        Assert.assertThat(data.description, `is`(dataTest.description))
        Assert.assertThat(data.latitude, `is`(dataTest.latitude))
        Assert.assertThat(data.longitude, `is`(dataTest.longitude))
        Assert.assertThat(data.id, `is`(dataTest.id))
        Assert.assertThat(data.title, `is`(dataTest.title))

        Repo.deleteAllReminders()

        val getAllRem = Repo.getReminders()
        assertThat(getAllRem is Result.Success, `is`(true))
        getAllRem as Result.Success
        assertThat(getAllRem.data, `is` (emptyList()))
    }




}