package com.epam.training.simplenotes.util

import java.util.concurrent.Executors

private val service = Executors.newSingleThreadExecutor()

fun <RESULT> execute(
    action: () -> RESULT,
    onSuccess: (RESULT) -> Unit = {},
    onError: (throwable: Throwable) -> Unit = {},
    doBefore: () -> Unit = {},
    doAfter: () -> Unit = {}
) {

    doBefore()

    service.submit {
        val result: RESULT

        try {
            result = action()
        } catch (e: Throwable) {
            onError(e)
            doAfter()

            return@submit
        }

        doAfter()
        onSuccess(result)
    }
}