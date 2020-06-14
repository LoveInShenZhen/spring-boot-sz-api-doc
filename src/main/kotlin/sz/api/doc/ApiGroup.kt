package sz.api.doc

import sz.api.doc.annotations.Desc
import sz.api.tools.escapeMarkdown

//
// Created by kk on 17/8/24.
//
@ExperimentalStdlibApi
class ApiGroup(@Desc("api 分组名称") val groupName: String) {

    @Desc("api 列表")
    var apiInfoList: MutableList<ApiInfo> = mutableListOf()

    fun toMarkdownStr(str: String): String {
        return str.escapeMarkdown()
    }
}
