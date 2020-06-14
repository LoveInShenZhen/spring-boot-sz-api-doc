package sz.api.reply

import sz.api.doc.annotations.Desc

//
// Created by kk on 2020/5/2.
//
open class ReplyBase {
    @Desc("操作返回值, 等于0时, 表示操作成功. 非0时, 表示操作失败")
    var ret = 0

    @Desc("错误信息. 操作成功时, 返回 OK. 操作失败时, 返回指定的错误信息.")
    var errmsg = "OK"

}