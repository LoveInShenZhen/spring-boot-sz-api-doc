package sz.api.controllers

import sz.api.doc.ApiGroup
import sz.api.doc.annotations.Desc
import sz.api.reply.ReplyBase

//
// Created by kk on 2020/5/9.
//
@ExperimentalStdlibApi
class ApiInfoReply : ReplyBase() {

    @Desc("api group 列表")
    var api_groups = listOf<ApiGroup>()
}