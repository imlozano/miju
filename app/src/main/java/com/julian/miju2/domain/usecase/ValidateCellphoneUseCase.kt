package com.julian.miju2.domain.usecase

import com.julian.miju2.R
import javax.inject.Inject

class ValidateCellphoneUseCase @Inject constructor() {
    operator fun invoke(cellphoneNumber: String): Int? {
        if (cellphoneNumber.length < 10) {
            return R.string.error_cellphone_short
        }
        if (cellphoneNumber.length > 10){
            return R.string.error_cellphone_long
        }
        if (!cellphoneNumber.startsWith("3")) {
            return R.string.error_cellphone_invalid_start
        }
        return null
    }
}
