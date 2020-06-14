package sz.api.doc

import com.fasterxml.jackson.annotation.JsonIgnore
import sz.api.doc.annotations.Desc
import sz.api.tools.escapeMarkdown


//
// Created by kk on 17/8/24.
//
class ParameterInfo {

    @Desc("参数名称")
    var name: String = ""

    @Desc("参数描述")
    var desc: String = ""

    @Desc("是否是必填参数")
    var required: Boolean = true

    @Desc("可选参数时,参数采用的默认值")
    var defaultValue: String = ""

    @Desc("参数的数据类型")
    var type: String = ""

    fun toMarkdownStr(str: String): String {
        return str.escapeMarkdown()
    }

    val fullDesc: String
        @JsonIgnore
        get() {
            val requiredDesc = if (required) "[必填]" else "[可选]"
            val full_desc = mutableListOf(desc, "[$type]", requiredDesc)
            if (defaultValue.isNotBlank()) {
                full_desc.add("[默认值: ${defaultValue}]")
            }
            return full_desc.joinToString(", ")
        }
}