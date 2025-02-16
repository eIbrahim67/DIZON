package com.eibrahim.dizon.core.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.eibrahim.dizon.core.response.FailureReason
import com.eibrahim.dizon.core.response.Response
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

object UtilsFunctions {

    fun <T> applyResponse(

        liveDate: MutableLiveData<Response<T>>,
        viewModelScope: CoroutineScope,
        dataFetch: suspend () -> T

    ) {

        liveDate.value = Response.Loading

        viewModelScope.launch {

            try {
                val result = dataFetch()
                liveDate.value = Response.Success(result)
            } catch (e: IOException) {
                liveDate.value = Response.Failure(FailureReason.NoInternet)
            } catch (e: Exception) {
                liveDate.value = Response.Failure(
                    FailureReason.UnknownError(
                        e.message ?: "Unknown error occurred"
                    )
                )
            }
        }
    }

    fun createMaterialAlertDialogBuilderOkCancel(
        context: Context,
        title: String,
        message: String,
        positiveBtnMsg: String,
        negativeBtnMsg: String,
        positiveBtnFun: () -> Unit
    ) {

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnMsg) { dialog, _ ->

                positiveBtnFun()

                dialog.dismiss()
            }
            .setNegativeButton(negativeBtnMsg) { dialog, _ ->

                dialog.dismiss()
            }
            .setCancelable(false)
            .show()

    }

    fun createMaterialAlertDialogBuilderOk(
        context: Context,
        title: String,
        message: String,
        positiveBtnMsg: String,
        positiveBtnFun: () -> Unit
    ) {

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnMsg) { dialog, _ ->

                positiveBtnFun()

                dialog.dismiss()
            }
            .setCancelable(false)
            .show()

    }

    private var isFailureReason_NoInternet: Boolean = false

    fun createFailureResponse(
        response: Response.Failure,
        context: Context
    ) {
        when (val failureReason = response.reason) {

            is FailureReason.NoInternet -> {
                if (!isFailureReason_NoInternet) {
                    isFailureReason_NoInternet = true
                    createMaterialAlertDialogBuilderOkCancel(
                        context,
                        title = "No Internet Connection",
                        message = "Please check your internet connection and try again.",
                        positiveBtnMsg = "Try again",
                        negativeBtnMsg = "Cancel"
                    ) {
                        //TODO Optionally, define any action to take after the dialog is dismissed
                    }
                }
            }

            is FailureReason.UnknownError -> {
                val errorMessage = failureReason.error
                createMaterialAlertDialogBuilderOk(
                    context,
                    title = "Unknown Error",
                    message = "An unknown error occurred: $errorMessage",
                    positiveBtnMsg = "OK"
                ) {

                }
            }
        }
    }
}