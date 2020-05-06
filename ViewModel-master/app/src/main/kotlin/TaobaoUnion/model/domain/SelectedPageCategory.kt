package TaobaoUnion.model.domain

 data class SelectedPageCategory(
    val code: Int,
    val `data`: List<Data2>,
    val message: String,
    val success: Boolean
)

data class Data2(
    val favorites_id: Int,
    val favorites_title: String,
    val type: Int
)