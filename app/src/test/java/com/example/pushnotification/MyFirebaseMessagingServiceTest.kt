package com.example.pushnotification

import android.widget.RemoteViews
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test


class MyFirebaseMessagingServiceTest {

    private lateinit var myFirebaseMessagingService: MyFirebaseMessagingService

    @MockK
     private lateinit var remoteViews: RemoteViews

    @Before
    fun setup(){
        MockKAnnotations.init(this)
        myFirebaseMessagingService= MyFirebaseMessagingService()
    }

    @Test
    fun getRemoteViewTest(){
      val remote= every   {
            myFirebaseMessagingService.getRemoteView("tittle","hey")
        } returns remoteViews
       assertThat(remote).isNotNull()
    }

    @Test
    fun generateNotification(){
        val generate= every {
            myFirebaseMessagingService.generateNotification("tittle","hi")
        }returns Unit
        assertThat(generate).isNotNull()
    }
}