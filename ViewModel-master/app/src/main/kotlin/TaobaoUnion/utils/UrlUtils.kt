package TaobaoUnion.utils

object UrlUtils {
    fun getDiscoveryContentUrl(categoryId: Int, page: Int): String {
        return "discovery/$categoryId/$page"
    }

    fun getCoverPath(pict_url:String):String{
        return if (pict_url.startsWith("http")||pict_url.startsWith("https")){
            pict_url
        }else{
            "https:$pict_url"
        }
    }

    fun getOnSellUrl(defaultPage: Int): String {
        return "onSell/$defaultPage"
    }

    fun getTypContentUrl(categoryId: Int): String {
        return "recommend/$categoryId"
    }
}