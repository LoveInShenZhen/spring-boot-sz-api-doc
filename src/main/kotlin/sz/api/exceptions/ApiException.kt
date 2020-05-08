package sz.api.exceptions

//
// Created by kk on 2020/5/7.
//

/**
 * 该异常用于描述遇到业务逻辑错误, 并且该错误可以被暴露给api接口的调用者
 */
class ApiException(errMsg: String = "", val errCode: Int = -1) : Exception(errMsg)