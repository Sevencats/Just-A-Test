package TaobaoUnion.model.domain

import com.adgvcxz.IModel

data class TicketResult(
        val code: Int,
        val `data`: Data1,
        val message: String,
        val success: Boolean
)

data class Data1(
        val tbk_tpwd_create_response: TbkTpwdCreateResponse
)

data class TbkTpwdCreateResponse(
        val `data`: DataX,
        val request_id: String
)

data class DataX(
        val model: String
)








