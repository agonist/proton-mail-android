/*
 * Copyright (c) 2020 Proton Technologies AG
 *
 * This file is part of ProtonMail.
 *
 * ProtonMail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonMail. If not, see https://www.gnu.org/licenses/.
 */

package ch.protonmail.android.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ch.protonmail.android.api.ProtonMailApiManager
import ch.protonmail.android.api.models.LabelBody
import ch.protonmail.android.api.models.messages.receive.LabelResponse
import ch.protonmail.android.data.LabelRepository

internal const val KEY_INPUT_DATA_LABEL_NAME = "keyInputDataLabelName"
internal const val KEY_INPUT_DATA_LABEL_ID = "keyInputDataLabelId"
internal const val KEY_INPUT_DATA_IS_UPDATE = "keyInputDataIsUpdate"
internal const val KEY_INPUT_DATA_LABEL_COLOR = "keyInputDataLabelColor"
internal const val KEY_INPUT_DATA_LABEL_DISPLAY = "keyInputDataLabelIsDisplay"
internal const val KEY_INPUT_DATA_LABEL_EXCLUSIVE = "keyInputDataLabelExclusive"
internal const val KEY_POST_LABEL_WORKER_RESULT_ERROR = "keyResultDataPostLabelWorkerError"

class PostLabelWorker @WorkerInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    private val apiManager: ProtonMailApiManager,
    private val labelRepository: LabelRepository
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val labelName = getLabelNameParam() ?: return Result.failure()
        val color = getLabelColorParam() ?: return Result.failure()
        val display = getDisplayParam()
        val exclusive = getExclusiveParam()

        val labelResponse = if (isUpdateParam()) {
            val validLabelId = getLabelIdParam() ?: return Result.failure()
            apiManager.updateLabel(validLabelId, LabelBody(labelName, color, display, exclusive))
        } else {
            apiManager.createLabel(LabelBody(labelName, color, display, exclusive))
        }

        if (labelResponse.hasError()) {
//            AppUtil.postEventOnUi(LabelAddedEvent(Status.FAILED, errorText))
            return Result.failure(workDataOf(KEY_POST_LABEL_WORKER_RESULT_ERROR to labelResponse.error))
        }

        if (hasInvalidLabelApiResponse(labelResponse)) {
//            AppUtil.postEventOnUi(LabelAddedEvent(Status.FAILED, labelResponse.error))
            return Result.failure(workDataOf(KEY_POST_LABEL_WORKER_RESULT_ERROR to labelResponse.error))
        }

        labelRepository.saveLabel(labelResponse.label)
//        AppUtil.postEventOnUi(LabelAddedEvent(Status.SUCCESS, null))
        return Result.success()
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun hasInvalidLabelApiResponse(labelResponse: LabelResponse) =
        labelResponse.label == null || labelResponse.label.id.isEmpty()

    private fun getLabelIdParam() = inputData.getString(KEY_INPUT_DATA_LABEL_ID)

    private fun getExclusiveParam() = inputData.getInt(KEY_INPUT_DATA_LABEL_EXCLUSIVE, 0)

    private fun getDisplayParam() = inputData.getInt(KEY_INPUT_DATA_LABEL_DISPLAY, 0)

    private fun getLabelColorParam() = inputData.getString(KEY_INPUT_DATA_LABEL_COLOR)

    private fun getLabelNameParam() = inputData.getString(KEY_INPUT_DATA_LABEL_NAME)

    private fun isUpdateParam() = inputData.getBoolean(KEY_INPUT_DATA_IS_UPDATE, false)

    class Enqueuer(private val workManager: WorkManager) {

        fun enqueue(labelName: String,
                    color: String,
                    display: Int? = 0,
                    exclusive: Int? = 0,
                    update: Boolean? = false,
                    labelId: String? = null): Operation {

            val postLabelWorker = OneTimeWorkRequestBuilder<PostLabelWorker>()
                .setInputData(workDataOf(
                    KEY_INPUT_DATA_LABEL_ID to labelId,
                    KEY_INPUT_DATA_LABEL_NAME to labelName,
                    KEY_INPUT_DATA_LABEL_COLOR to color,
                    KEY_INPUT_DATA_LABEL_EXCLUSIVE to exclusive,
                    KEY_INPUT_DATA_IS_UPDATE to update,
                    KEY_INPUT_DATA_LABEL_DISPLAY to display
                ))
                .build()

            return workManager.enqueue(postLabelWorker)
        }
    }

}
