package sz.api.doc

import sz.api.doc.annotations.Comment
import sz.api.tools.escapeMarkdown

//
// Created by kk on 17/8/24.
//
@ExperimentalStdlibApi
class ApiGroup(@Comment("api 分组名称") val groupName: String) {

    @Comment("api 列表")
    var apiInfoList: MutableList<ApiInfo> = mutableListOf()

    fun toMarkdownStr(str: String): String {
        return str.escapeMarkdown()
    }
}
