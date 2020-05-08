package sz.api.resolve

import sz.api.doc.ApiGroup

//
// Created by kk on 2020/5/8.
//
@ExperimentalStdlibApi
interface IDefinedApis {

    fun apiGroups(): List<ApiGroup>
}